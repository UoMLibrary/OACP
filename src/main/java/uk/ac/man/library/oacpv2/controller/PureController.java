package uk.ac.man.library.oacpv2.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Date;
import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.stereotype.Controller;

import uk.ac.man.library.oacpv2.model.Audit;
import uk.ac.man.library.oacpv2.model.Author;
import uk.ac.man.library.oacpv2.model.Note;
import uk.ac.man.library.oacpv2.model.Publication;
import uk.ac.man.library.oacpv2.model.User;
import uk.ac.man.library.oacpv2.objects.pure.Purerecord;
import uk.ac.man.library.oacpv2.repository.AuditRepository;
import uk.ac.man.library.oacpv2.repository.AuthorRepository;
import uk.ac.man.library.oacpv2.repository.NoteRepository;
import uk.ac.man.library.oacpv2.repository.PublicationRepository;
import uk.ac.man.library.oacpv2.objects.pure.AuthorResponse;

import org.springframework.web.client.HttpClientErrorException;

@SessionAttributes("purerecord")
@Controller
public class PureController {

	private static final Logger logger = LoggerFactory.getLogger(PureController.class.getName());

	private final PublicationRepository publicationRepository;
	private final AuthorRepository authorRepository;
	private final AuditRepository auditRepository;
	private final NoteRepository noteRepository;
	@Autowired
	public PureController(PublicationRepository publicationRepository, AuthorRepository authorRepository,
			AuditRepository auditRepository,NoteRepository noteRepository) {
		this.publicationRepository = publicationRepository;
		this.authorRepository = authorRepository;
		this.auditRepository = auditRepository;
		this.noteRepository = noteRepository;
	}

	@Value("${pure.apikey}")
	String pureApikey;

	@Value("${pure.url}")
	String pureUrl;

	@Value("${uni.url}")
	String UniUrl;

	@Value("${api.username}")
	String username;

	@Value("${api.password}")
	String password;

	String pureFileds = "pureId,title.value,type.term.text.value, publicationStatuses.*, "
			+ "personAssociations.*.*,info.*, journalAssociation.*,"
			+ "electronicVersions.doi, embargoEndDate.value, publisher.*";

	@GetMapping("/puredata")
	public ModelAndView getPureId(HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		HttpSession session = request.getSession(false);
		if (session != null) {

			if (session.getAttribute("isValid") == "yes") {
				User user = (User) session.getAttribute("user");

				logger.info("pure/create_record, logged username: " + user.getFullName());
				modelAndView.addObject("user", user);
				modelAndView.setViewName("pure/create_record");
			} else {
				logger.info("No Valid");
				modelAndView.setViewName("redirect:/");
			}

		} else {
			logger.info("No session");
			modelAndView.setViewName("redirect:/");
		}

		return modelAndView;
	}

//retrieve pure record from PAIS:
	@GetMapping("/publication")
//	public ModelAndView getPureData(@RequestParam(name = "pureId") String pureId, @ModelAttribute Publication publication){
	public ModelAndView getPureData(@RequestParam(name = "pureId", required = false) String pureId,
			                          HttpServletRequest request) {
		
		ModelAndView modelAndView = new ModelAndView();
		
			//check session for authentication
			HttpSession session = request.getSession(false);
			if(session != null) {
				if(session.getAttribute("isValid") == "yes") {
					User user = (User) session.getAttribute("user");
					logger.info("pure/create_record, logged username: " + user.getFullName());
					modelAndView.addObject("user", user);
					
//					check if @RequestParam is null or not.
					if(pureId != null && pureId != "") {
						String Pureid = pureId.trim();
						logger.info("USING THE PURE ID, Search local database:" + Pureid);
						
						// 3.1 USING THE PURE ID, Search local database
						if (publicationRepository.findBypureId(Pureid) != null) {
							
//If the record exists, display the message and move into the edit workflow 
							modelAndView.addObject("editpure", Pureid);
							modelAndView.setViewName("pure/create_record");

						} else {
//retrieve the relevant Pure record using the Pure’s web service and populate the record creation form with this data. 
							Purerecord PureRecord = getPureRecord(pureId);

							if (PureRecord.getPureId() == null) {
								modelAndView.addObject("error", "Cannot find record from PURE API!");
								modelAndView.setViewName("pure/create_record");
							} else {
								modelAndView.addObject("purerecord", PureRecord);
								modelAndView.setViewName("pure/response_page");
							}

						}
					}else {
						//end if pure is null or empty
						modelAndView.addObject("error", "Please input Pureid!");
						modelAndView.setViewName("pure/create_record");
					}
					
					
					
				}else {
					//end if isValid
					logger.info("No Valid");
					modelAndView.setViewName("redirect:/");
//					
				}
					
			}else {
				//end if session, redirected to cas login
				logger.info("/publication No session");
				modelAndView.setViewName("redirect:/");
			}
			
		return modelAndView;
	}

	@GetMapping("/publication/sync/{pureid}")
	@ResponseBody
	public String publicationSync(@PathVariable("pureid") String pureId,
			  						HttpServletRequest request) {
		String responseMes = "";
		HttpSession session = request.getSession(false);
		if(session != null) {
			if(session.getAttribute("isValid") == "yes") {
				
				User user = (User) session.getAttribute("user");
				logger.info("publication/sync, logged username: " + user.getFullName());
				
				//process main code 
				// get all information about pure record form pure api.
				// and set all information to local record. 13 fields:
				Purerecord pureRecord = getPureRecord(pureId);
				if(pureRecord.getPureId()!=null) {
					
					logger.info("Start Sync: " +  pureId);
					boolean isSync = false;
					//start audit log.
					// retriever publication record form local
					Publication publication = publicationRepository.findBypureId(pureId);
					
					String auditJson = "";
					auditJson = "{\"Last_Sync_Date\":{\"type\": \"update\"}";

					if (!Objects.equals(publication.getTitle(), pureRecord.getTitle())) {
						publication.setTitle(pureRecord.getTitle());
						isSync = true;
						String title = pureRecord.getTitle();
						title = title.replaceAll("[^a-zA-Z0-9]", " "); 
						auditJson += ",\"Title\":{\"type\": \"Sync\",\"data\": \"" + title + "\"}";
					}

					if (!Objects.equals(publication.getOutputType(), pureRecord.getOutputType())) {
						publication.setOutputType(pureRecord.getOutputType());
						isSync = true;
						auditJson += ",\"OutputType\":{\"type\": \"Sync\",\"data\": \"" + pureRecord.getOutputType() + "\"}";
					}

					if (!Objects.equals(publication.getPublicationStatus(), pureRecord.getPublicationStatus())) {
						publication.setPublicationStatus(pureRecord.getPublicationStatus());
						isSync = true;
						auditJson += ",\"Publication_Status\":{\"type\": \"Sync\",\"data\": \"" + pureRecord.getPublicationStatus()
								+ "\"}";
					}
					
					if (!Objects.equals(publication.getJournal(), pureRecord.getJournal())) {
						publication.setJournal(pureRecord.getJournal());
						isSync = true;
						auditJson += ",\"Journal\":{\"type\": \"Sync\",\"data\": \"" + pureRecord.getJournal() + "\"}";
					}

					if (!Objects.equals(publication.getDoi(), pureRecord.getDoi())) {
						publication.setDoi(pureRecord.getDoi());
						isSync = true;
						String doi= pureRecord.getDoi();
						auditJson += ",\"Doi\":{\"type\": \"Sync\",\"data\": \"" + doi + "\"}";
					}
					
					if (!Objects.equals(publication.getPublisherName(), pureRecord.getPublisherName())) {
						publication.setPublisherName(pureRecord.getPublisherName());
						isSync = true;
						auditJson += ",\"Publisher_Name\":{\"type\": \"Sync\",\"data\": \"" + pureRecord.getPublisherName() + "\"}";
					}


					if (!Objects.equals(publication.getPortalUrl(), pureRecord.getPortalUrl())) {
						publication.setPortalUrl(pureRecord.getPortalUrl());
						isSync = true;
						String url = pureRecord.getPortalUrl();
						auditJson += ",\"Portal_Url\":{\"type\": \"Sync\",\"data\": \"" + url + "\"}";
					}
					
					if (!Objects.equals(publication.getCreatedBy(), pureRecord.getCreatedBy())) {
						publication.setCreatedBy(pureRecord.getCreatedBy());
						isSync = true;
						auditJson += ",\"Created_By\":{\"type\": \"Sync\",\"data\": \"" + pureRecord.getCreatedBy() + "\"}";
					}
					
					if (CompareTwoDate(publication.getAcceptedDate(),pureRecord.getAcceptedDate())==1) {
						publication.setAcceptedDate(pureRecord.getAcceptedDate());
						isSync = true;
						auditJson += ",\"Accepted_Date\":{\"type\": \"Sync\",\"data\": \"" + pureRecord.getAcceptedDate() + "\"}";
					}
					
					if (CompareTwoDate(publication.getePublicationDate(),pureRecord.getePublicationDate())==1) {
						publication.setePublicationDate(pureRecord.getePublicationDate());
						isSync = true;
						auditJson += ",\"E_Publication_Date\":{\"type\": \"Sync\",\"data\": \"" + pureRecord.getePublicationDate() + "\"}";
					}
					
					if (CompareTwoDate(publication.getPublicationDate(),pureRecord.getPublicationDate())==1) {
						publication.setPublicationDate(pureRecord.getPublicationDate());
						isSync = true;
						auditJson += ",\"Publication_Date\":{\"type\": \"Sync\",\"data\": \"" + pureRecord.getPublicationDate() + "\"}";
					}

					if (CompareTwoDate(publication.getCreatedDate(),pureRecord.getCreatedDate()) == 1) {
						publication.setCreatedDate(pureRecord.getCreatedDate());
						isSync = true;
						auditJson += ",\"Created_Date\":{\"type\": \"Sync\",\"data\": \"" + pureRecord.getCreatedDate() + "\"}";
					}
					
					if (CompareTwoDate(publication.getPure_last_modified_date(),pureRecord.getPure_last_modified_date()) == 1) {
						publication.setPure_last_modified_date(pureRecord.getPure_last_modified_date());
						isSync = true;
						auditJson += ",\"Pure_Last_Modified_Date\":{\"type\": \"Sync\",\"data\": \"" + pureRecord.getPure_last_modified_date() + "\"}";
					}

					
					//compare authors:
					Set<Author> AuthorModelList = new HashSet<>();
					int i =0;
					for (AuthorResponse authorRes: pureRecord.getAuthors()) {
						i++;
						logger.info("sync:compare authors length: " + pureRecord.getAuthors().size());
						Author authornew = new Author();
						
						//compare exist author
						if(authorRes.getId()!= null) {
							authornew = authorRepository.findById(authorRes.getId()).get();
							
							if(!Objects.equals(authornew.getForename(), authorRes.getForename())) {
								authornew.setForename(authorRes.getForename());
								isSync = true;
								auditJson += ",\"Exist_Author_Forename_"+i+"\":{\"type\": \"Sync\""
												+ ",\"Forename\": \"" + authorRes.getForename() + "\"}";
							}
							if(!Objects.equals(authornew.getSurname(),authorRes.getSurname())) {
								authornew.setSurname(authorRes.getSurname());
								isSync = true;
								auditJson += ",\"Exist_Author_Surname_"+i+"\":{\"type\": \"Sync\""
										+ ",\"Surname\": \"" + authorRes.getSurname() + "\"}";
							}
							
							if(!Objects.equals(authornew.getFaculty_name(),authorRes.getFaculty_name())) {
								authornew.setFaculty_name(authorRes.getFaculty_name());
								authornew.setFaculty_id(authorRes.getFaculty_id());
								isSync = true;
								auditJson += ",\"Exist_Author_Faculty_"+i+"\":{\"type\": \"Sync\""
										+ ",\"Faculty\": \"" + authorRes.getFaculty_name() + "\"}";
							}
							
							if(!Objects.equals(authornew.getSchool_name(),authorRes.getSchool_name())) {
								authornew.setSchool_id(authorRes.getSchool_id());
								authornew.setSchool_name(authorRes.getSchool_name());
								isSync = true;
								auditJson += ",\"Exist_Author_School_"+i+"\":{\"type\": \"Sync\""
										+ ",\"School\": \"" + authorRes.getSchool_name() + "\"}";
							}
						}else {
							//add new author
							isSync = true;
							authornew = AuthorfromAuthorRes(authornew,authorRes);
							authorRepository.save(authornew);
							auditJson += ",\"New_Author_"+i+"\":{\"type\": \"Sync_new\",\"Forename\": \"" + authorRes.getForename() 
													+"\",\"Surname\": \"" + authorRes.getSurname()
													+"\",\"Faculty_name\": \"" + authorRes.getFaculty_name()
													+"\",\"School_name\": \"" + authorRes.getSchool_name()
													+ "\"}";
						}
						AuthorModelList.add(authornew);
						
					}
					
					if(isSync) {
						auditJson += "}";
						publication.setAuthors(AuthorModelList);
						publication.setOacp_syncedBy(user.getFullName());
						publication.setOacp_syncedDate(new Date());
						
						try {

							// save sync authors information to db;
							publicationRepository.save(publication);
							String type = "PUBLICATION_PURE_SYNC_RECORD_UPDATE";
							String message = "Sync complete for record " + publication.getPureId();
							CreateAudit(user.getFullName(), type, message,"LOG", auditJson);
							
							
						} catch (Exception e) {
							// TODO: handle exception
							logger.info("submitForm, save publication, publication is null: " + publication);
						}
						responseMes = "sync";
//						session.setAttribute("message", "sync");
						//end of sync 
					}else {
						responseMes = "syncuptodate";
//						session.setAttribute("message", "syncuptodate");
					}
					
				}else{
					logger.info("when syn, pureId do not find in pure API: " + pureId);
					//create audit log 
					responseMes = "syncnopureID";
//					session.setAttribute("message", "syncnopureID");
					
					String type= "PUBLICATION_PURE_SYNC_RECORD_UPDATE_FAILED";
					String message = "Sync failed for record " + pureId;
					String auditJson = "{\"message\": \"Pure record not found: " + pureId 
										+"\", \"response\": \" 404\"}";
					CreateAudit(user.getFullName(), type, message,"ERROR", auditJson);
				}
				
			 
					// return to edit view page.
//				modelAndView.setViewName("redirect:/publications/"+ pureId); 

			}else {
				logger.info("No valid");
				responseMes = "No valid";
			}
		}else {
			logger.info("No session");
			responseMes = "No session";
		}
		
		return responseMes;
		
	}

// create a new OACP record. save data locally!!
	@PostMapping("/publication/create")
	public ModelAndView submitForm(@ModelAttribute(value = "purerecord") Purerecord pureRecord,
				HttpServletRequest request,
				@RequestParam(value = "notelist", required = false) String[] notelist) {
		ModelAndView modelAndView = new ModelAndView();
		HttpSession session = request.getSession(false);
		if(session != null) {
			if(session.getAttribute("isValid") == "yes") {
				User user = (User) session.getAttribute("user");
				logger.info("pure/create_record, logged username: " + user.getFullName());
				modelAndView.addObject("user", user);	
			// proceed.......... main code
				Publication publication = new Publication();
				// convert from response object to publication model. all fields
				convertPublicationFromPureRecord(publication, pureRecord);
				setoacpPublication(publication, pureRecord);
				Date createDate = new Date();
				publication.setOacp_createdBy(user.getFullName());
				publication.setOacp_createdDate(createDate);


				for (AuthorResponse authorRes: pureRecord.getAuthors()) {
					Author authornew = new Author();
					authornew = AuthorfromAuthorRes(authornew, authorRes);
					if(authornew.getId() != null) {
						publication.addAuthor(authornew);
					}else {
						authorRepository.save(authornew);
						publication.addAuthor(authornew);
						
					}
					 
				}
				
				
				Publication pu = publicationRepository.save(publication);
				
				if(notelist!=null) {
					for(String note: notelist) {
						Note newNote = new Note();
						newNote.setCreated_date(new Date());
						newNote.setMessage(note);
						newNote.setDisplayName(user.getFullName());
						newNote.setUsername(user.getUserName());
						newNote.setPublication(pu);
						noteRepository.save(newNote);
					}
				}
				
				String type = "PUBLICATION_RECORD_CREATE";
				String message = "Created new OACP record:" + pu.getPureId();
				String auditJson = "Created new OACP record:" + pu.getPureId();
				CreateAudit(user.getFullName(),type, message,"INFO", auditJson);
				
				session.setAttribute("message", "create");
				modelAndView.setViewName("redirect:/publications/" + pu.getPureId());
				
			}else {
				logger.info("No valid");
				modelAndView.setViewName("error-pages/403");
			}
		}else {
			logger.info("No session");
			modelAndView.setViewName("redirect:/");
		}

		return modelAndView;
	}

//delete pure record from local database:
	@GetMapping(path = "/publication/delete/{pureid}")
	@ResponseBody
	public String deletePublicationsbyPureId(@PathVariable("pureid") String pureid,
			HttpServletRequest request) {
		
		HttpSession session = request.getSession(true);
		
		if(session != null) {
			if(session.getAttribute("isValid") == "yes") {
				User user = (User) session.getAttribute("user");
				try {
					Publication publication = publicationRepository.findBypureId(pureid);
					
					for(Note note: publication.getNote_list()) {
						noteRepository.delete(note);
					}
					
					String type = "PUBLICATION_RECORD_DELETE";
					String message = "Deleted OACP record:" + pureid;
					String auditJson = "Deleted OACP record:" + pureid;
					CreateAudit(user.getFullName(),type, message,"INFO", auditJson);
					
					publicationRepository.delete(publication);

				} catch (Exception e) {
					// TODO: handle exception
					logger.error("delete Publication byPureId, Exception: " + e);
					logger.error("delete Publication byPureId, pureid: " + pureid);
				}
				session.setAttribute("message", "delete");
				return "delete";
			}else {
				logger.info("No Valid");
				return "No Valid";
			}
		}else {
			logger.info("No session");
			return "No session";
		}
		
	}
	
	@PostMapping("/publication/deletenote/{noteId}")
	@ResponseBody
	public String pureDeletenote(@PathVariable("noteId") String id,
			HttpServletRequest request) {
		
		HttpSession session = request.getSession(false);
		
		if (session != null) {
			if (session.getAttribute("isValid") == "yes") {
				User user = (User) session.getAttribute("user");
				
				Optional<Note> deleteNote = noteRepository.findById(id);
				noteRepository.delete(deleteNote.get());
				
				return "note delete successful!";
				
			}else {
				logger.info("No Valid");
				return "No Valid";
			}
			
		}else {
			logger.info("No session");
			return "No session";
		}
		
	}
	
	
// edit PURE record from local database:
	@PostMapping("/publication/update")
	@ResponseBody
	public ArrayList<String> pureUpdate(final @ModelAttribute(value = "purerecord") Publication pureRecord,
							HttpServletRequest request,
							@RequestParam(value = "notelist", required = false) String[] notelist
							) {
		logger.info("pure id:" + pureRecord.getPureId());
		ArrayList<String> result = new ArrayList<String>();
		HttpSession session = request.getSession(false);
		if(session != null) {
		if(session.getAttribute("isValid") == "yes") {
				User user = (User) session.getAttribute("user");
				logger.info("pure/create_record, logged username: " + user.getFullName());
			// proceed.......... main code

				Publication pubbyID = publicationRepository.findBypureId(pureRecord.getPureId());

					boolean isUpdate = false;
					String auditJson = "";
					auditJson = "{\"last_modified_date\":{\"type\": \"UPDATED\"}";

					// update related information edited by oacp users, other fields from pure apis
					// keep the same.
					if (!Objects.equals(pubbyID.getDeposit_route(), pureRecord.getDeposit_route())) {
						pubbyID.setDeposit_route(pureRecord.getDeposit_route());
						isUpdate = true;
						auditJson += ",\"Deposit_route\":{\"type\": \"UPDATED\",\"data\": \"" + pureRecord.getDeposit_route()
								+ "\"}";

					}

					if (!Objects.equals(pubbyID.getCompliance_status(), pureRecord.getCompliance_status())) {
						isUpdate = true;
						pubbyID.setCompliance_status(pureRecord.getCompliance_status());
						auditJson += ",\"Compliance_status\":{\"type\": \"UPDATED\",\"data\": \""
								+ pureRecord.getCompliance_status() + "\"}";
					}
					
					if(pubbyID.isPotentially_published()==true) {
						String beforeStatus = pubbyID.getRecord_status();
						String afterStatus = pureRecord.getRecord_status();
						if(beforeStatus.equals("AAM deposited - awaiting publication") 
								&& afterStatus.equals("AAM deposited - published")) {
							result.add("PotentiallyFalse");
							pubbyID.setPotentially_published(false);
							auditJson += ",\"Potentially_published\":{\"type\": \"UPDATED\",\"data\": \"false\"}";
						}
					}
					

					if (!Objects.equals(pubbyID.getRecord_status(), pureRecord.getRecord_status())) {
						pubbyID.setRecord_status(pureRecord.getRecord_status());
						auditJson += ",\"Record_status\":{\"type\": \"UPDATED\",\"data\": \"" + pureRecord.getRecord_status()
								+ "\"}";
						isUpdate = true;
					}
					

					if (!Objects.equals(pubbyID.getGateway_depositor(), pureRecord.getGateway_depositor())) {
						pubbyID.setGateway_depositor(pureRecord.getGateway_depositor());
						auditJson += ",\"Gateway_depositor\":{\"type\": \"UPDATED\",\"data\": \""
								+ pureRecord.getGateway_depositor() + "\"}";
						isUpdate = true;
					}

					if (pubbyID.isHelp_raise_visibility() != pureRecord.isHelp_raise_visibility()) {
						pubbyID.setHelp_raise_visibility(pureRecord.isHelp_raise_visibility());
						auditJson += ",\"Help_raise_visibility\":{\"type\": \"UPDATED\",\"data\": \""
								+ pureRecord.isHelp_raise_visibility() + "\"}";
						isUpdate = true;
					}

					if (pubbyID.isRights_retention_statement() != pureRecord.isRights_retention_statement()) {
						pubbyID.setRights_retention_statement(pureRecord.isRights_retention_statement());
						auditJson += ",\"Rights_retention_statement\":{\"type\": \"UPDATED\",\"data\": \""
								+ pureRecord.isRights_retention_statement() + "\"}";
						isUpdate = true;
					}

					if (pubbyID.isData_access_statement() != pureRecord.isData_access_statement()) {
						pubbyID.setData_access_statement(pureRecord.isData_access_statement());
						auditJson += ",\"Data_access_statement\":{\"type\": \"UPDATED\",\"data\": \""
								+ pureRecord.isData_access_statement() + "\"}";
						isUpdate = true;
					}
					
					if(pubbyID.isRef_compliance_exception() != pureRecord.isRef_compliance_exception()) {
						pubbyID.setRef_compliance_exception(pureRecord.isRef_compliance_exception());
						auditJson += ",\"REF_compliance_exception\":{\"type\": \"UPDATED\",\"data\": \""
								+ pureRecord.isRef_compliance_exception() + "\"}";
						isUpdate = true;
					}
					
					if(pubbyID.isUkri_S_Flag() != pureRecord.isUkri_S_Flag()) {
						pubbyID.setUkri_S_Flag(pureRecord.isUkri_S_Flag());
						auditJson += ",\"UKRI_S_Flag\":{\"type\": \"UPDATED\",\"data\": \""
								+ pureRecord.isUkri_S_Flag() + "\"}";
						isUpdate = true;
					}

					if (!Objects.equals(pubbyID.getEmbargo_length(), pureRecord.getEmbargo_length())) {
						pubbyID.setEmbargo_length(pureRecord.getEmbargo_length());
						auditJson += ",\"Embargo_length\":{\"type\": \"UPDATED\",\"data\": \"" + pureRecord.getEmbargo_length()
								+ "\"}";
						isUpdate = true;
					}
					
					if (!Objects.equals(pubbyID.getUkri_compliance_status(), pureRecord.getUkri_compliance_status())) {
						pubbyID.setUkri_compliance_status(pureRecord.getUkri_compliance_status());
						auditJson += ",\"UKRI_compliance_status\":{\"type\": \"UPDATED\",\"data\": \"" + pureRecord.getUkri_compliance_status()
								+ "\"}";
						isUpdate = true;
					}
//					
					if (!Objects.equals(pubbyID.getFileUrl(), pureRecord.getFileUrl())) {
						pubbyID.setFileUrl(pureRecord.getFileUrl());
						auditJson += ",\"FileUrl\":{\"type\": \"UPDATED\",\"data\": \"" + pureRecord.getFileUrl()
								+ "\"}";
						isUpdate = true;
					}
					
					if(notelist!=null) {
					auditJson += ",\"Add_Note\":{\"type\": \"UPDATED\"" ;
					int i = 0;
					for(String note: notelist) {
						i++;
						isUpdate = true;
						Note newNote = new Note();
						newNote.setCreated_date(new Date());
						newNote.setMessage(note);
						newNote.setDisplayName(user.getFullName());
						newNote.setUsername(user.getUserName());
						newNote.setPublication(pubbyID);
						
						note = note.replaceAll("[^a-zA-Z0-9]", " "); 	
						auditJson += ",\"Note_"+i+"\": \"" + note +"\"";
						
						noteRepository.save(newNote);
					}
					auditJson +="}";
					}
					auditJson +="}";
		//code for audit information;
					if(isUpdate) {
						
						pubbyID.setOacp_modifiedBy(user.getFullName());
						pubbyID.setOacp_modifiedDate(new Date());
						publicationRepository.save(pubbyID);
						
						String type = "PUBLICATION_RECORD_UPDATE";
						String message = "Updated publication record " + pubbyID.getPureId();
						CreateAudit(user.getFullName(),type, message,"LOG", auditJson);
						
						
						result.add("update");
						session.setAttribute("message", "update");
//						modelAndView.setViewName("redirect:/publications/" + pubbyID.getPureId());
					}else {
						result.add("updatenodata");
						session.setAttribute("message", "updatenodata");
//						modelAndView.setViewName("redirect:/publications/" + pubbyID.getPureId());
					}
					
			}else {
				logger.info("No valid");
				result.add("No valid");
//				modelAndView.setViewName("error-pages/403");
			}
		}else {
			logger.info("No session");
			result.add("No session");
//			modelAndView.setViewName("redirect:/");
		}

		return result;
	}
	
	
	
	
//get all publication from local db
	@GetMapping(path = "/publications/all")
	public ModelAndView getPublications(ModelAndView modelAndView, 
						HttpServletRequest request) {
		
		
		HttpSession session = request.getSession(false);
		if (session != null){
			if (session.getAttribute("isValid") != null){
				User user = (User) session.getAttribute("user");

				logger.info("pure/create_record, logged username: " + user.getFullName());
				modelAndView.addObject("user", user);
				
				String pureID = "";
				if(request.getParameter("pureId") != null) {
					pureID = request.getParameter("pureId");
				}
				
				if(session.getAttribute("message") != null) {
					String message = (String) session.getAttribute("message");
					session.removeAttribute("message");
					modelAndView.addObject("message",message);
				}
				
				modelAndView.addObject("pureId", pureID);
				modelAndView.setViewName("pure/list_publications");
				
			}else {
				logger.info("No Valid");
				modelAndView.setViewName("redirect:/");
			}
				
		}else {
			//end if session null
			logger.info("No session");
			modelAndView.setViewName("redirect:/");
		}
		

		return modelAndView;

	}
	
	
	
	@GetMapping(path = "/publications/{pureid}")
	public ModelAndView getPublicationsbyPureId(@PathVariable("pureid") String id, 
										ModelAndView modelAndView,
										HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if(session != null) {
		if(session.getAttribute("isValid") == "yes") {
				User user = (User) session.getAttribute("user");
				logger.info("pure/create_record, logged username: " + user.getFullName());
				modelAndView.addObject("user", user);	
			// proceed.......... main code
				Publication publications = new Publication();
				try {
					publications = publicationRepository.findBypureId(id);
				} catch (Exception e) {
					// TODO: handle exception
					logger.info("getPublicationsbyPureId, get publication, id is null: " + id);
				}
				
				if(session.getAttribute("message") != null) {
					String message = (String) session.getAttribute("message");
					session.removeAttribute("message");
			System.out.println("edit record:" + message);
					modelAndView.addObject("message",message);
				}
				modelAndView.addObject("purerecord", publications);

				modelAndView.setViewName("pure/edit_view");
				
			}else {
				logger.info("No valid");
				modelAndView.setViewName("error-pages/403");
			}
		}else {
			logger.info("No session");
			modelAndView.setViewName("redirect:/");
		}

		return modelAndView;

	}
	
	

	// Define a public function to get pureRecord Object from pure API
	public Purerecord getPureRecord(String pureId) {
		// 3.1 USING THE PURE ID, get pure record form pure API.
		Purerecord purerecordNew = new Purerecord();
		// Call the pure API
		try {
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.set("Accept", "application/json");
			final String urlString = pureUrl + "research-outputs/" + pureId + "?fields=" + pureFileds + "&apiKey="
					+ pureApikey;
			logger.info("urlString" + urlString);
			purerecordNew = restTemplate.getForObject(urlString, Purerecord.class);
		} catch (HttpClientErrorException e) {

			// print error to log later
			logger.info("When calling the pure api, catch HttpClientErrorException: " + pureId);

		} catch (Exception e) {
			logger.info("When calling the pure api, catch Exception: " + e);
//			return modelAndView;
		} 

		// get information about publisher name form journal api.
		try {
			
			final String JournalUrl = pureUrl + "journals/" + purerecordNew.getJournal_uuid()
					+ "?fields=publisher.name.text.*&apiKey=" + pureApikey;
			HttpHeaders PERSONheaders = new HttpHeaders();
			HttpEntity request = new HttpEntity(PERSONheaders);

			ResponseEntity<String> response = new RestTemplate().exchange(JournalUrl, HttpMethod.GET, request,
					String.class);

			String json = response.getBody();
			JSONObject journaljson = new JSONObject(json);

			if (journaljson.has("publisher")) {

				JSONArray publishArrayList = journaljson.getJSONObject("publisher").getJSONObject("name")
						.getJSONArray("text");

				JSONObject publisher = new JSONObject(publishArrayList.get(0).toString());


				purerecordNew.setPublisherName(publisher.getString("value"));
			}

		} catch (Exception e) {
			// TODO: handle exception
			logger.info("error when call journal api for publisher: ");
		}

		// get information about authors
		ArrayList<AuthorResponse> authorrecord = new ArrayList<>();
		if (purerecordNew.getAuthors() != null) {
			for (AuthorResponse authorResponse: purerecordNew.getAuthors()) {
			
				// check the DB if the author is already exist.
				if (authorRepository.findByUuid(authorResponse.getUuid()).size() > 0) {
					Author author = authorRepository.findByUuid(authorResponse.getUuid()).get(0);
					logger.info("author exist: " + author.getForename());
					authorResponse.setId(author.getId());
					authorResponse.setEmailAddress(author.getEmailAddress());
					authorResponse.setFaculty_id(author.getFaculty_id());
					authorResponse.setFaculty_name(author.getFaculty_name());
					authorResponse.setSchool_id(author.getSchool_id());
					authorResponse.setSchool_name(author.getSchool_name());
					authorResponse.setSpotID(author.getSpotID());
					authorResponse.setUuid(author.getUuid());
				} else {

					// Call the person APIS by uuid
					try {
						
						
						final String urlPurePerson = UniUrl + "purePerson/" + authorResponse.getUuid();

						HttpHeaders PERSONheaders = new HttpHeaders();
						PERSONheaders.setBasicAuth(username, password);
						HttpEntity request = new HttpEntity(PERSONheaders);

						ResponseEntity<String> response = new RestTemplate().exchange(urlPurePerson, HttpMethod.GET,
								request, String.class);
						String json = response.getBody();
						JSONObject personjson = new JSONObject(json);
						if (personjson.has("SPOTID")) {
							authorResponse.setSpotID(personjson.get("SPOTID").toString());
						}

						if (personjson.has("emailAddress")) {
							authorResponse.setEmailAddress(personjson.get("emailAddress").toString());
						}

						// Call the Organisation APIs by externalId
						final String urlschoolFaculty = UniUrl + "schoolFaculty/" + authorResponse.getOrganisationID();

						ResponseEntity<String> Facultyresponse = new RestTemplate().exchange(urlschoolFaculty,
								HttpMethod.GET, request, String.class);
						String faculty = Facultyresponse.getBody();
						JSONObject Facultyjson = new JSONObject(faculty);

						if (Facultyjson.has("school_name")) {
							authorResponse.setSchool_name(Facultyjson.get("school_name").toString());
							authorResponse.setSchool_id(Facultyjson.get("school_id").toString());
						}
						if (Facultyjson.has("faculty_name")) {
							authorResponse.setFaculty_name(Facultyjson.get("faculty_name").toString());
							authorResponse.setFaculty_id(Facultyjson.get("faculty_id").toString());
						}

					} catch (HttpClientErrorException | JSONException e) {
						logger.info("When call person API, there is no author information with UUID:"
								+ authorResponse.getUuid());

					}

				}

				authorrecord.add(authorResponse);

			}

		}

		purerecordNew.setAuthors(authorrecord);

		return purerecordNew;
	}

	// this function used to converted data form pureRecord(pure api) to
	// publication(local MODEL db).
	// reset publication from pure api.
	public Publication convertPublicationFromPureRecord(Publication publication, Purerecord pureRecord) {
		
		publication.setPureId(pureRecord.getPureId());
		publication.setTitle(pureRecord.getTitle());
		publication.setOutputType(pureRecord.getOutputType());
		publication.setPublicationStatus(pureRecord.getPublicationStatus());
		publication.setAcceptedDate(pureRecord.getAcceptedDate());
		publication.setPublicationDate(pureRecord.getPublicationDate());
		publication.setePublicationDate(pureRecord.getePublicationDate());
		publication.setJournal(pureRecord.getJournal());
		publication.setDoi(pureRecord.getDoi());
		publication.setCreatedBy(pureRecord.getCreatedBy());
		publication.setCreatedDate(pureRecord.getCreatedDate());
		publication.setPublisherName(pureRecord.getPublisherName());
		publication.setPortalUrl(pureRecord.getPortalUrl());
		publication.setPure_last_modified_date(pureRecord.getPure_last_modified_date());
		return publication;

	}

	// this function used to reset publication field edited by oacp users.
	public Publication setoacpPublication(Publication publication, Purerecord pureRecord) {

		publication.setCompliance_status(pureRecord.getCompliance_status());
		publication.setDeposit_route(pureRecord.getDeposit_route());
		publication.setFileUrl(pureRecord.getFileUrl());
		publication.setGateway_depositor(pureRecord.getGateway_depositor());
		
		publication.setHelp_raise_visibility(pureRecord.isHelp_raise_visibility());
		publication.setData_access_statement(pureRecord.isData_access_statement());
		publication.setRights_retention_statement(pureRecord.isRights_retention_statement());
		publication.setUkri_S_Flag(pureRecord.isUkri_S_Flag());
		publication.setRef_compliance_exception(pureRecord.isRef_compliance_exception());
		publication.setUkri_compliance_status(pureRecord.getUkri_compliance_status());
		
		publication.setOacp_createdBy(pureRecord.getOacp_createdBy());
		publication.setOacp_createdDate(pureRecord.getOacp_createdDate());
		publication.setOacp_modifiedBy(pureRecord.getOacp_modifiedBy());
		publication.setOacp_modifiedDate(pureRecord.getOacp_modifiedDate());
		publication.setOacp_syncedBy(pureRecord.getOacp_syncedBy());
		publication.setOacp_syncedDate(pureRecord.getOacp_syncedDate());
		publication.setRecord_status(pureRecord.getRecord_status());
//		publication.setNotes(pureRecord.getNotes());
		publication.setEmbargo_length(pureRecord.getEmbargo_length());
		
		return publication;
	}

	public Author AuthorfromAuthorRes(Author author, AuthorResponse authorRes) {
		author.setId(authorRes.getId());
		author.setEmailAddress(authorRes.getEmailAddress());
		author.setFaculty_name(authorRes.getFaculty_name());
		author.setFaculty_id(authorRes.getFaculty_id());
		author.setSchool_id(authorRes.getSchool_id());
		author.setForename(authorRes.getForename());
		author.setSchool_name(authorRes.getSchool_name());
		author.setSpotID(authorRes.getSpotID());
		author.setSurname(authorRes.getSurname());
		author.setUuid(authorRes.getUuid());
		return author;
	}
	
	public int CompareTwoDate(Date d1, Date d2) {
		if(d1 == null && d2 == null){ 
			return 0;
		}else if(d1 == null && d2 != null) {
			return 1;
		}else if(d1 != null && d2 == null) {
			return 0;
		}else
			return d1.compareTo(d2);
		
	}
	
	
	public void CreateAudit(String user, String type, String message, String level, String updateDetail) {
		Audit audit = new Audit();
		if(updateDetail == "") {
			updateDetail = "No data was recorded for this event";
		}
		audit.setCreated_by(user);
		audit.setCreated_date(new Date());
		audit.setType(type);
		audit.setLevel(level);
		audit.setMessage(message);
		audit.setUpdateDetail(updateDetail);
		auditRepository.save(audit);
	}
 

}
