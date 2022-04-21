package uk.ac.man.library.oacpv2.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import uk.ac.man.library.oacpv2.model.Publication;
import uk.ac.man.library.oacpv2.model.User;
import uk.ac.man.library.oacpv2.objects.pure.PublicationsColumns;
import uk.ac.man.library.oacpv2.objects.pure.publicationsDTO;
import uk.ac.man.library.oacpv2.repository.AuditRepository;
import uk.ac.man.library.oacpv2.repository.AuthorRepository;
import uk.ac.man.library.oacpv2.repository.JdbcPublicationRepository;
import uk.ac.man.library.oacpv2.repository.PublicationRepository;
import uk.ac.man.library.oacpv2.service.PublicationExcelExporter;


@RestController
public class PureResponseController {
	private static final Logger logger = LoggerFactory.getLogger(PureResponseController.class.getName());

	private final PublicationRepository publicationRepository;
	private final AuthorRepository authorRepository;
	private final AuditRepository auditRepository;
	
	@Autowired
	public PureResponseController(PublicationRepository publicationRepository, AuthorRepository authorRepository,
			AuditRepository auditRepository,
			JdbcPublicationRepository jdbcPubRep) {
		this.publicationRepository = publicationRepository;
		this.authorRepository = authorRepository;
		this.auditRepository = auditRepository;
	}
	
	
	@GetMapping(path = "/publication/list")
	public ResponseEntity<publicationsDTO> getDatatableList(HttpServletRequest request,
			@RequestParam("start") int start,
			@RequestParam("length") int length,
			@RequestParam("draw") int draw
			) {
		
		HttpSession session = request.getSession(false);
		if (session != null) {
			if (session.getAttribute("isValid") == "yes") {
				//filter column's value
				String pureID = "";
				String publicationStatus = "";
				String record_status = "";
				String doi = "";
				String compliance_status = "";
				String ukri_compliance_status = "";
				String organization = "";
				
				
				if(request.getParameter("record_status") != "") {
					record_status = request.getParameter("record_status").trim();
				}
				
				if(request.getParameter("pureId") != "") {
					pureID = request.getParameter("pureId").trim();
				}
				
				if(request.getParameter("publicationStatus") != "") {
					publicationStatus = request.getParameter("publicationStatus");
				}
				
				if(request.getParameter("doi") != "") {
					doi = request.getParameter("doi").trim();
				}
				
				if(request.getParameter("compliance_status") != "") {
					compliance_status = request.getParameter("compliance_status");
				}
				
				if(request.getParameter("UKRI_compliance_status") != "") {
					ukri_compliance_status = request.getParameter("UKRI_compliance_status");
				}
				
				if(request.getParameter("organization") != "") {
					organization = request.getParameter("organization");
				}
				 
				String school_id ="";
				String faculty_id ="";
				
				if(request.getParameter("organizaitonlevel") != null) {
					if(request.getParameter("organizaitonlevel").equals("2")) {
						faculty_id=organization;
					}else if(request.getParameter("organizaitonlevel").equals("3")) {
						school_id=organization;
					}
				}
				
				String potentially_published = request.getParameter("potentially_published");
				String UKRI_S_Flag = request.getParameter("UKRI_S_Flag");
				String help_raise_visibility = request.getParameter("help_raise_visibility");
				
				List<Boolean> publishedList =  new ArrayList<>();
				List<Boolean> flagList =  new ArrayList<>();
				List<Boolean> helpList =  new ArrayList<>();
				if(potentially_published.isEmpty()) {
					publishedList.add(Boolean.FALSE);
					publishedList.add(Boolean.TRUE);
				}else {
					publishedList.add(Boolean.valueOf(potentially_published));
				}
				
				if(UKRI_S_Flag.isEmpty()) {
					flagList.add(Boolean.FALSE);
					flagList.add(Boolean.TRUE);
				}else {
					flagList.add(Boolean.valueOf(UKRI_S_Flag));
				}
				if(help_raise_visibility.isEmpty()) {
					helpList.add(Boolean.FALSE);
					helpList.add(Boolean.TRUE);
				}else {
					helpList.add(Boolean.valueOf(help_raise_visibility));
				}
				//pagination and sorting
				String orderstr = request.getParameter("order[0][dir]");
				String orderNo = request.getParameter("order[0][column]");
				String ordercol = "columns["+orderNo+"][data]";
				String orderColumn = request.getParameter(ordercol);
				

				Order order = new Order(getSortDirection(orderstr), orderColumn);
				int page = start/length;
				Pageable pageable = PageRequest.of(page, length,Sort.by(order));
				
				
				publicationsDTO publicationDto = new publicationsDTO();
				
				Page<Publication> publicationPage;
				
				publicationPage = publicationRepository.PublicationFiler(pureID,publicationStatus,
						record_status,compliance_status,ukri_compliance_status,
						publishedList,flagList,helpList,doi,
						school_id,faculty_id,
						pageable);
				
				List<PublicationsColumns> dataColumns= new ArrayList<PublicationsColumns>();
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				
				for(Publication pu: publicationPage) {
					PublicationsColumns publication = new PublicationsColumns();
					
					publication.setPureId(pu.getPureId());
					publication.setPublicationStatus(pu.getPublicationStatus());
					publication.setTitle(pu.getTitle());
					publication.setRecord_status(pu.getRecord_status());
					publication.setCompliance_status(pu.getCompliance_status());
					publication.setUkri_compliance_status(pu.getUkri_compliance_status());
					
					if(pu.getAcceptedDate() != null) {
						publication.setAcceptedDate(sdf.format(pu.getAcceptedDate()));
						
					}else {
						publication.setAcceptedDate("-");
					}
					
					if(pu.getePublicationDate() != null) {
						publication.setePublicationDate(sdf.format(pu.getePublicationDate()));
						
					}else {
						publication.setePublicationDate("-");
					}
					
					if(pu.getPublicationDate() != null) {
						publication.setPublicationDate(sdf.format(pu.getPublicationDate()));
						
					}else {
						publication.setPublicationDate("-");
					}
					
					if(pu.isPotentially_published() == true) {
						publication.setPotentially_published("✓");
					}else {
						publication.setPotentially_published("");
					}
					
					dataColumns.add(publication);
				}
				
				publicationDto.setData(dataColumns);
				publicationDto.setDraw(draw);
				publicationDto.setRecordsFiltered(publicationPage.getTotalElements());
				publicationDto.setRecordsTotal(publicationPage.getTotalElements());
				return ResponseEntity.ok(publicationDto);
			}else {
				logger.info("No Valid");
				return null;
			}
		}else {
			logger.info("No session");
			return null;
		}
		
		
		
	}
	
	
	
	@GetMapping(path = "/publication/export")
	public void DownloadExcel(HttpServletResponse response,
			HttpServletRequest request)
			throws IOException {
		HttpSession session = request.getSession(false);
		if(session != null) {
		if(session.getAttribute("isValid") == "yes") {
				User user = (User) session.getAttribute("user");
				logger.info("/download, logged username: " + user.getFullName());
			// proceed.......... main code
				//filter column's value
				String pureID = "";
				String publicationStatus = "";
				String record_status = "";
				String doi = "";
				String compliance_status = "";
				String UKRI_compliance_status = "";
				String organization = "";
				String action="";
				
				if(request.getParameter("pureId") != null) {
					pureID = request.getParameter("pureId").trim();
				}
				if(request.getParameter("action") != null) {
					action = request.getParameter("action").trim();
				}
				
				if(request.getParameter("publicationStatus") != null) {
					publicationStatus = request.getParameter("publicationStatus");
				}
				
				if(request.getParameter("record_status") != null) {
					doi = request.getParameter("doi").trim();
				}
				
				if(request.getParameter("compliance_status") != null) {
					compliance_status = request.getParameter("compliance_status");
				}
				
				if(request.getParameter("UKRI_compliance_status") != null) {
					UKRI_compliance_status = request.getParameter("UKRI_compliance_status");
				}
				
				if(request.getParameter("organization") != null) {
					organization = request.getParameter("organization");
				}
				
				
				String school_id = "";
				String faculty_id = "";

				if (organization != "" & organization != null) {
					String subString = organization.substring(1, 2);
					if (subString.equals("2")) {
						faculty_id = organization;
					}

					if (subString.equals("3")) {
						school_id = organization;
					}
				}

				
				
				List<Boolean> publishedList =  new ArrayList<>();
				List<Boolean> flagList =  new ArrayList<>();
				List<Boolean> helpList =  new ArrayList<>();
				if(request.getParameter("potentially_published") == null) {
					publishedList.add(Boolean.FALSE);
					publishedList.add(Boolean.TRUE);
				}else {
					publishedList.add(Boolean.valueOf(request.getParameter("potentially_published")));
				}
				
				
				if(request.getParameter("UKRI_S_Flag") == null) {
					flagList.add(Boolean.FALSE);
					flagList.add(Boolean.TRUE);
				}else {
					flagList.add(Boolean.valueOf(request.getParameter("UKRI_S_Flag")));
				}
				
				if(request.getParameter("help_raise_visibility") == null) {
					helpList.add(Boolean.FALSE);
					helpList.add(Boolean.TRUE);
				}else {
					helpList.add(Boolean.valueOf(request.getParameter("help_raise_visibility")));
				}
				
				
				
				response.setContentType("application/octet-stream");
				DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
				String currentDateTime = dateFormatter.format(new Date());

				String headerKey = "Content-Disposition";
				String headerValue = "attachment; filename=OACP_" + currentDateTime + ".xlsx";
				response.setHeader(headerKey, headerValue);
				List<Publication> listPublications =  
						publicationRepository.ExportByPureid(pureID,publicationStatus,
								record_status,compliance_status,UKRI_compliance_status,
								publishedList,flagList,helpList,
								doi,school_id,faculty_id);
				
				PublicationExcelExporter excelExporter = new PublicationExcelExporter(listPublications);
				
				
				if(action.equals("perPureId")) {
					logger.info("export per pureid size: " + listPublications.size());
					excelExporter.export(response);
				}
				
				if(action.equals("perAuthor")) {
					logger.info("export per author size: " + listPublications.size());
					excelExporter.exportperAuthor(response);
					
				}
				
				
			}else {
				logger.info("No valid");
			}
		}else {
			logger.info("No session");
		}

	}
	
	
	
	
	 private Sort.Direction getSortDirection(String direction) {
		    if (direction.equals("asc")) {
		      return Sort.Direction.ASC;
		    } else if (direction.equals("desc")) {
		      return Sort.Direction.DESC;
		    }
		    return Sort.Direction.ASC;
		  }
	 

}
