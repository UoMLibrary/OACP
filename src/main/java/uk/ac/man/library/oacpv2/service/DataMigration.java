package uk.ac.man.library.oacpv2.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import uk.ac.man.library.oacpv2.controller.PureController;
import uk.ac.man.library.oacpv2.model.Audit;
import uk.ac.man.library.oacpv2.model.Author;
import uk.ac.man.library.oacpv2.model.Note;
import uk.ac.man.library.oacpv2.model.Publication;
import uk.ac.man.library.oacpv2.objects.pure.AuthorResponse;
import uk.ac.man.library.oacpv2.objects.pure.Purerecord;
import uk.ac.man.library.oacpv2.repository.AuditRepository;
import uk.ac.man.library.oacpv2.repository.AuthorRepository;
import uk.ac.man.library.oacpv2.repository.NoteRepository;
import uk.ac.man.library.oacpv2.repository.PublicationRepository;

@Component
public class DataMigration {

	private static final Logger logger = LoggerFactory.getLogger(DataMigration.class.getName());

	private final PublicationRepository publicationRep;
	private final PureController pureController;
	private final AuthorRepository authorRepository;
	private final NoteRepository noteRepository;
	private final AuditRepository auditRepository;
	
	
	@Autowired
	public DataMigration(PublicationRepository publicationRep, PureController pureController,
			AuthorRepository authorRepository,NoteRepository noteRepository,AuditRepository auditRepository
			) {
		this.publicationRep = publicationRep;
		this.pureController = pureController;
		this.authorRepository = authorRepository;
		this.noteRepository = noteRepository;
		this.auditRepository = auditRepository;
	}

//	schedule every day at 2:00am
//	@Scheduled(cron = "0 35 07 * * ?", zone = "Europe/London")
//	@Scheduled(cron = "0 52 08 * * ?", zone = "Europe/London")
	public void PublicationmigrationSync(){

		// get pureid list from local database, and retrieve all pure id
		List<String> pureIdList = publicationRep.getAllPureID();
		int number=0;
		logger.info("Publication migrationSync: start: " + new Date());
		String message = "Data Migration with Pure APIs Start";
		AddAudit("LOG", message);
		
		for (String pureid: pureIdList) {
			logger.info("Publication migration Sync " + pureid);
			Purerecord pureRecord = pureController.getPureRecord(pureid);
			
			Publication publication = publicationRep.findBypureId(pureid);

			// update publication meta data, and then add authors and organization.
			if (pureRecord.getPureId() != null) {
				

				// convert from response object to publication model. all fields
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
				
				
				Set<Author> AuthorModelList = new HashSet<>();
				for (int j = 0; j < pureRecord.getAuthors().size(); j++) {
					AuthorResponse authorRes = pureRecord.getAuthors().get(j);
					Author authornew = new Author();
					authornew = pureController.AuthorfromAuthorRes(authornew, authorRes);

					AuthorModelList.add(authornew);

				}
				
				try {
					authorRepository.saveAll(AuthorModelList);
				}catch(Exception e) {
			    	 logger.info("migrate data and save authors, author is null: " + AuthorModelList);
			     }
				
				
				publication.setAuthors(AuthorModelList);
				
	     try {
	    	 publicationRep.save(publication);
	    	 number++;
	     }catch(Exception e) {
	    	 logger.info("migrate data and save publication: " + publication.getPureId());
	     }
			
		}else {
			
			String messageerr = "Data migration, pureId not founded from pure API: " + pureid;
			AddAudit("ERROR", messageerr);
			
		}

	}
	String messageend = "Publication migration length: END: " + number;
	AddAudit("LOG", messageend);
logger.info("Publication migrationSync: END: " + new Date());
	}
	
//	@Scheduled(cron = "0 11 17 * * ?", zone = "Europe/London")
	public void PublicationNotesJson() throws ParseException{
		
		// get pureid list from local database, and retrieve all pure id
		List<Publication> pureIdList = publicationRep.getByNote();
		logger.info("migrate note, Start: " + new Date());
		int number = 0;
		for(Publication publication: pureIdList) {
			if(publication.getNote_temp() != null) {
				
				JSONArray jsonArray = new JSONArray();
				
				try {
					jsonArray = new JSONArray(publication.getNote_temp()); 
				}catch(org.json.JSONException exception){
					logger.info("invalid json format: " + publication.getPureId());
				} 
				
				
				//check if node is not null
				if(jsonArray.length() > 0 ) {
					List<Note> note_list = new ArrayList<>();
					 for(int i=0 ; i<jsonArray.length();i++) {
						 

						 Note note = new Note();
						 try{
							 JSONObject jsonObj = jsonArray.getJSONObject(i);
							 

								 String messageList = jsonObj.getJSONArray("message").get(0).toString();
								 
								 
								 SimpleDateFormat formatter =  new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
								 Date date = formatter.parse((String)jsonObj.getJSONObject("created_date").get("$date"));
								 note.setCreated_date(date);
								 note.setDisplayName(jsonObj.getString("displayName"));
								 note.setMessage(messageList);
								 note.setUsername(jsonObj.getString("username"));
								 note.setPublication(publication);
								 note_list.add(note);
							 
							}catch(org.json.JSONException exception){
								logger.info("invalid json format: " + publication.getPureId());
							} 
						 
						
					 }
 System.out.println("note_list length: " + note_list.size());		
 
					 noteRepository.saveAll(note_list);
					 
					 publication.setNote_list(note_list);
					  try {
					    	 publicationRep.save(publication);
					    	 number++;
					     }catch(Exception e) {
					    	 logger.info("migrate note: " + publication.getPureId());
					     }
					 
				}
			}
			
		}
	logger.info("migrate note length: " + number);
	logger.info("migrate note END: " + new Date());
	}
	
	
	public void AddAudit(String level, String message) {
		Audit auditSync = new Audit();
		auditSync.setCreated_by("SYSTEM");
		auditSync.setCreated_date(new Date());
		auditSync.setType("PUBLICATION_PURE_DATA_MIGRATE");
		auditSync.setLevel(level);
		auditSync.setMessage(message);
		auditSync.setUpdateDetail("NO DATA FOUND");
		auditRepository.save(auditSync);
	}

}
