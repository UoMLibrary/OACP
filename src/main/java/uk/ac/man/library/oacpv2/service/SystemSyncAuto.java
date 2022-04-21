package uk.ac.man.library.oacpv2.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import info.debatty.java.stringsimilarity.JaroWinkler;
import uk.ac.man.library.oacpv2.controller.PureController;
import uk.ac.man.library.oacpv2.model.Audit;
import uk.ac.man.library.oacpv2.model.Author;
import uk.ac.man.library.oacpv2.model.Publication;
import uk.ac.man.library.oacpv2.objects.pure.AuthorResponse;
import uk.ac.man.library.oacpv2.objects.pure.Purerecord;
import uk.ac.man.library.oacpv2.objects.scholarcy.Elements;
import uk.ac.man.library.oacpv2.objects.scholarcy.RefResponse;
import uk.ac.man.library.oacpv2.objects.scholarcy.ResponseElements;
import uk.ac.man.library.oacpv2.repository.AuditRepository;
import uk.ac.man.library.oacpv2.repository.AuthorRepository;
import uk.ac.man.library.oacpv2.repository.PublicationRepository;

@Component
public class SystemSyncAuto {

	private static final Logger logger = LoggerFactory.getLogger(SystemSyncAuto.class.getName());

	private final AuditRepository auditRepository;
	private final PublicationRepository publicationRep;
	private final PureController pureController;
	private final AuthorRepository authorRepository;
	
	@Value("${crossRefURL}")
	String crossRefURL;
	
	@Autowired
	public SystemSyncAuto(AuditRepository auditRepository, PublicationRepository publicationRep,
			PureController pureController, AuthorRepository authorRepository) {
		this.auditRepository = auditRepository;
		this.publicationRep = publicationRep;
		this.pureController = pureController;
		this.authorRepository = authorRepository;
	}

//	daily at 1AM for any records created within the last two months 
	 @Scheduled(cron = "00 00 01 * * ?" , zone = "Europe/London")
	public void PublicationSyncTwoMonth() throws InterruptedException {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -2);
		Date date = cal.getTime();
		List<String> pureIdList = publicationRep.getRecordWithin2Months(date);
		logger.info("system sysnc pureIdList, pureIdList length: " + pureIdList.size());

		// sync started time, and log
		String auditJson = "{\"Daily_sync_Records_size\": \""+pureIdList.size()+"\"}";
		CreateAudit("PUBLICATION_PURE_SYNC_STARTED","Publication syncer records started.","LOG",auditJson);

		
		for (String pureId: pureIdList) {
			SyncPublication(pureId);
		}

		// sync completed time, and log
		logger.info("publication auto sync end:" + new Date());
		
		CreateAudit("PUBLICATION_PURE_SYNC_COMPLETE","Publication syncer complete","LOG","");

		
	}
	 
	 
	
//	 All the records last Saturday of every month 8PM 
	 @Scheduled(cron = "00 00 20 * * 6L" , zone = "Europe/London")
//	 @Scheduled(cron = "00 41 15 * * ?" , zone = "Europe/London")
	public void PublicationSyncAuto() throws InterruptedException {

		List<String> pureIdList = publicationRep.getAllPureID();
		logger.info("system sysnc pureIdList, pureIdList length: " + pureIdList.size());

		// sync started time, and log
		String detail = "{\"Monthly_Basis_sync_Records_size\": \""+pureIdList.size()+"\"}";
		CreateAudit("PUBLICATION_PURE_SYNC_STARTED","Publication syncer all records started","LOG",detail);

		for (String pureId: pureIdList) {
			SyncPublication(pureId);
		}

		// sync completed time, and log
		logger.info("publication auto sync end:" + new Date());
		
		CreateAudit("PUBLICATION_PURE_SYNC_COMPLETE","Publication syncer complete","LOG","");

	}
	
//	CrossRef Every Sun at 9PM 
	@Scheduled(cron = "00 00 21 * * SUN", zone = "Europe/London")
//	@Scheduled(cron = "00 49 15 * * ?", zone = "Europe/London")
	public void PublicationSyncCrossRef() throws InterruptedException {
		
		JaroWinkler jw = new JaroWinkler();
		String status = "AAM deposited - awaiting publication";
		List<Publication> publicationList = publicationRep.PublicationWaitingToBePublished(status);
		String detail = "{\"Crossref_sync_Records_size\": \""+publicationList.size()+"\"}";
		CreateAudit("CROSSREF_SYNC_STARTED","Crossref sync started","LOG",detail);
		
		for(Publication publication: publicationList) {
			
			if(publication.getDoi() != null && !publication.getDoi().trim().isEmpty()) {
				publication.setPotentially_published(true);
				publicationRep.save(publication);
				String message = "Marked " +  publication.getPureId() + " as potentially published: " + publication.getDoi();
				CreateAudit("CROSSREF_SYNC_MARKED_AS_POTENTIALLY_PUBLISHED",message,"LOG","");
			}else {
				
				String title = publication.getTitle();
				
				if(!title.isEmpty()) {
					
						try {
							RefResponse refRes = new RefResponse();
							RestTemplate restTemplate = new RestTemplate();
							HttpHeaders headers = new HttpHeaders();
							headers.set("Accept", "application/json");
							final String urlString = crossRefURL + "?query.title=" + URLEncoder.encode(title, StandardCharsets.UTF_8);
							logger.info("urlString" + urlString);
							refRes = restTemplate.getForObject(urlString, RefResponse.class);
							logger.info("crossRef for pureID: " + publication.getPureId());
							ResponseElements responseItem = refRes.getMessage();
							Elements item = responseItem.getItems().get(0);
							
							if(jw.similarity(title, item.getTitle().get(0))>0.8) {
								
								if(item.getDoi()!="") {
									logger.info("this records is poteitically published!!!"+publication.getPureId());
									publication.setPotentially_published(true);
									publication.setPotentially_doi(item.getDoi());
									publicationRep.save(publication);
									String message = "Marked " +  publication.getPureId() + " as potentially published: " + item.getDoi();
									CreateAudit("CROSSREF_SYNC_MARKED_AS_POTENTIALLY_PUBLISHED",message,"LOG","");
								}else {
									logger.info("Found publication in the Crossref API but it does not have a DOI!"+publication.getPureId());
									String message = "Found publication in the Crossref API but it does not have a DOI!"+publication.getPureId();
									CreateAudit("CROSSREF_SYNC_NOT_FOUND", message, "ERROR","");
								}
								
							}else {
								
								logger.info("Cannot find publication in the Crossref API" + publication.getPureId());
								String message = "Cannot find publication in the Crossref API: " + publication.getPureId();
								CreateAudit("CROSSREF_SYNC_NOT_FOUND", message, "ERROR","");
							}
							
						}catch (Exception e) {
							
							logger.info("Crossref request failed: " + publication.getPureId());
							String message = "Crossref request failed: " + publication.getPureId();
							CreateAudit("CROSSREF_SYNC_NOT_FOUND", message, "ERROR","");
						} 
					
					
				}else {
					String message = "This pure is no Title: "+publication.getPureId();
					CreateAudit("CROSSREF_SYNC_NOT_FOUND", message, "ERROR","");
				}
				
			}
			
		}
		
		CreateAudit("CROSSREF_SYNC_COMPLETE","Crossref sync complete","LOG","");
	}
	
	
	public void CreateAudit(String type, String message, String level,String updateDetail) {
		
		if(updateDetail=="") {
			updateDetail="No data was recorded for this event";
		}
		Audit audit = new Audit();
		audit.setCreated_by("SYSTEM");
		audit.setCreated_date(new Date());
		audit.setType(type);
		audit.setLevel(level);
		audit.setMessage(message);
		audit.setUpdateDetail(updateDetail);
		auditRepository.save(audit);
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
	
	
	public void SyncPublication(String pureId) {
		
		boolean isSync = false;
		Purerecord pureRecord = pureController.getPureRecord(pureId);
		Publication publication = publicationRep.findBypureId(pureId);

		if (pureRecord.getPureId() != null) {

			// compare local all fields of publication with pure fields from apis.
			String auditJson = "";
			auditJson = "{\"Last_Sync_Date\":{\"type\": \"update\"}";

			if (!Objects.equals(publication.getTitle(), pureRecord.getTitle())) {
				isSync=true;
				publication.setTitle(pureRecord.getTitle());
				String title = pureRecord.getTitle();
				title = title.replaceAll("[^a-zA-Z0-9]", " "); 
				auditJson += ",\"Title\":{\"i\": \"Sync\",\"data\": \"" + title + "\"}";
			}

			if (!Objects.equals(publication.getOutputType(), pureRecord.getOutputType())) {
				isSync=true;
				publication.setOutputType(pureRecord.getOutputType());
				auditJson += ",\"Output_Type\":{\"type\": \"Sync\",\"data\": \"" + pureRecord.getOutputType() + "\"}";
			}

			if (!Objects.equals(publication.getPublicationStatus(), pureRecord.getPublicationStatus())) {
				isSync=true;
				publication.setPublicationStatus(pureRecord.getPublicationStatus());
				auditJson += ",\"Publication_Status\":{\"type\": \"Sync\",\"data\": \"" + pureRecord.getPublicationStatus()
						+ "\"}";
			}
			
			if (!Objects.equals(publication.getJournal(), pureRecord.getJournal())) {
				isSync=true;
				publication.setJournal(pureRecord.getJournal());
				auditJson += ",\"Journal\":{\"type\": \"Sync\",\"data\": \"" + pureRecord.getJournal() + "\"}";
			}

			if (!Objects.equals(publication.getDoi(), pureRecord.getDoi())) {
				isSync=true;
				publication.setDoi(pureRecord.getDoi());
				auditJson += ",\"Doi\":{\"type\": \"Sync\",\"data\": \"" + pureRecord.getDoi() + "\"}";
			}
			
			if (!Objects.equals(publication.getPublisherName(), pureRecord.getPublisherName())) {
				isSync=true;
				publication.setPublisherName(pureRecord.getPublisherName());
				auditJson += ",\"Publisher_Name\":{\"type\": \"Sync\",\"data\": \"" + pureRecord.getPublisherName() + "\"}";
			}


			if (!Objects.equals(publication.getPortalUrl(), pureRecord.getPortalUrl())) {
				isSync=true;
				publication.setPortalUrl(pureRecord.getPortalUrl());
				auditJson += ",\"Portal_Url\":{\"type\": \"Sync\",\"data\": \"" + pureRecord.getPortalUrl() + "\"}";
			}
			
			if (!Objects.equals(publication.getCreatedBy(), pureRecord.getCreatedBy())) {
				isSync=true;
				publication.setCreatedBy(pureRecord.getCreatedBy());
				auditJson += ",\"Created_By\":{\"type\": \"Sync\",\"data\": \"" + pureRecord.getCreatedBy() + "\"}";
			}
			
			if (CompareTwoDate(publication.getAcceptedDate(),pureRecord.getAcceptedDate())==1) {
				isSync=true;
				publication.setAcceptedDate(pureRecord.getAcceptedDate());
				auditJson += ",\"Accepted_Date\":{\"type\": \"Sync\",\"data\": \"" + pureRecord.getAcceptedDate() + "\"}";
			}
			
			if (CompareTwoDate(publication.getePublicationDate(),pureRecord.getePublicationDate())==1) {
				isSync=true;
				publication.setePublicationDate(pureRecord.getePublicationDate());
				auditJson += ",\"E_Publication_Date\":{\"type\": \"Sync\",\"data\": \"" + pureRecord.getePublicationDate() + "\"}";
			}
			
			if (CompareTwoDate(publication.getPublicationDate(),pureRecord.getPublicationDate())==1) {
				isSync=true;
				publication.setPublicationDate(pureRecord.getPublicationDate());
				auditJson += ",\"Publication_Date\":{\"type\": \"Sync\",\"data\": \"" + pureRecord.getPublicationDate() + "\"}";
			}
//

			if (CompareTwoDate(publication.getCreatedDate(),pureRecord.getCreatedDate()) == 1) {
				isSync=true;
				publication.setCreatedDate(pureRecord.getCreatedDate());
				auditJson += ",\"Created_Date\":{\"type\": \"Sync\",\"data\": \"" + pureRecord.getCreatedDate() + "\"}";
			}
			
			if (CompareTwoDate(publication.getPure_last_modified_date(),pureRecord.getPure_last_modified_date()) == 1) {
				isSync=true;
				publication.setPure_last_modified_date(pureRecord.getPure_last_modified_date());
				auditJson += ",\"Pure_Last_Modified_Date\":{\"type\": \"Sync\",\"data\": \"" + pureRecord.getPure_last_modified_date() + "\"}";
			}
			
			//compare authors:
			Set<Author> AuthorModelList = new HashSet<>();
			int i = 0;
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
					authornew = pureController.AuthorfromAuthorRes(authornew,authorRes);
					authorRepository.save(authornew);
					auditJson += ",\"New_Author_"+i+"\":{\"type\": \"Sync_new\",\"Forename\": \"" + authorRes.getForename() 
						+"\",\"Surname\": \"" + authorRes.getSurname()
						+"\",\"Faculty_name\": \"" + authorRes.getFaculty_name()
						+"\",\"School_name\": \"" + authorRes.getSchool_name()
						+ "\"}";
				}
				AuthorModelList.add(authornew);
				
			}

			if (isSync) {
				auditJson += "}";
				publication.setAuthors(AuthorModelList);
				publication.setOacp_modifiedBy("SYSTEM");
				publication.setOacp_modifiedDate(new Date());
				
				publicationRep.save(publication);
				
				String type = "PUBLICATION_PURE_SYNC_RECORD_UPDATE";
				String message = "Publication syncer complete " + pureId;
				String level = "LOG";
				CreateAudit(type, message,level,auditJson);
				
			} 

		}else{

//there is no information founded by pureid from pure api.
			logger.info("sysnc with error, no record founded: " + pureId);
			String auditJson = "{\"message\":\"Pure record not found: " + pureId + "\"}";
			
			String message = "Sync failed for record " + pureId;
			CreateAudit("PUBLICATION_PURE_SYNC_RECORD_UPDATE_FAILED", message,"ERROR",auditJson);

		}
	}

}
