package uk.ac.man.library.oacpv2.objects.pure;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.management.loading.PrivateClassLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import uk.ac.man.library.oacpv2.objects.pure.AuthorResponse;

public class Purerecord {

	private Long id;

	@JsonProperty("pureId")
	private String pureId;

	private String title;

	@JsonProperty("title")
	private void unpackTitle(Map<String, String> title) {
		// System.out.println("title="+title.keySet());
		this.title = title.get("value");
	}

	private String outputType;

	@JsonProperty("type")
	private void unpackOutputtype(Map<String, Object> type) {
		
		if(type.keySet().contains("term")) {
			Map<String, Object> typeTerm = (Map) type.get("term");
			ArrayList typeTermText = (ArrayList) typeTerm.get("text");
			Map<String, String> typeTermTextMap = (Map) typeTermText.get(0);
			// System.out.println("typeTermTextMapValue=" + typeTermTextMap.get("value"));
			this.outputType = typeTermTextMap.get("value");
		}
		
	}

	private String publicationStatus;
	private Date acceptedDate;
	private Date publicationDate;
	private Date ePublicationDate;

	@JsonProperty("publicationStatuses")
	private void unpackPublicationStatuses(ArrayList publicationStatusList) throws ParseException {
		// ArrayList publicationStatusesList =
		// (ArrayList)publicationStatuses.get("text");

		for (int i = 0; i < publicationStatusList.size(); i++) {

			String tempDate = null;

			Map<String, Object> publicationStatusItem = (Map) (publicationStatusList.get(i));

			Map<String, Object> publicationDate = (Map) publicationStatusItem.get("publicationDate");

			if (publicationDate.get("year") != null) {
				tempDate = publicationDate.get("year").toString();
			} else {
				tempDate = "0000";
			}

			if (publicationDate.get("month") != null) {
				tempDate = tempDate + "/" + publicationDate.get("month").toString();

			} else {
				tempDate = tempDate + "/01";
			}

			if (publicationDate.get("day") != null) {
				tempDate = tempDate + "/" + publicationDate.get("day").toString();
			} else {
				tempDate = tempDate + "/01";
			}
			
			Date date = null;
			if (tempDate != null) {

				SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);

				date = formatter.parse(tempDate);

			}

			Map<String, Object> publicationStatus = (Map) publicationStatusItem.get("publicationStatus");
			Map<String, Object> publicationStatusTerm = (Map) publicationStatus.get("term");
			ArrayList publicationStatusTermText = (ArrayList) publicationStatusTerm.get("text");
			Map<String, String> publicationStatusTermTextMap = (Map) publicationStatusTermText.get(0);

			if (publicationStatusItem.get("current").toString().equalsIgnoreCase("true")) {
				this.publicationStatus = publicationStatusTermTextMap.get("value");
			}
			if (publicationStatusTermTextMap.get("value").toString().equalsIgnoreCase("Accepted/In press")) {
				// this.acceptedDate = publicationDate.get("year").toString();
				this.acceptedDate = date;
			} else if (publicationStatusTermTextMap.get("value").toString().equalsIgnoreCase("Published")) {
				// this.publicationDate = publicationDate.get("year").toString();
				this.publicationDate = date;
			} else if (publicationStatusTermTextMap.get("value").toString().equalsIgnoreCase("E-pub ahead of print")) {
				// this.ePublicationDate = publicationDate.get("year").toString();
				this.ePublicationDate = date;
			}

		}
	}

	private String journal;
	private String journal_uuid="";

	@JsonProperty("journalAssociation")
	private void unpackJournal(Map<String, Object> journalAssociation) {
//		Map<String, String> journal = (Map)journalAssociation.get("journal");
//		this.journal = journal.get("externalId");
		Map<String, Object> journalname = (Map) journalAssociation.get("title");

//		Map<String, String> journal = (Map)journalAssociation.get("title");
//System.out.println("journal:" + journal);
		this.journal = (String) journalname.get("value");
		if(journalAssociation.keySet().contains("journal")) {
			Map<String, Object> journaluuid = (Map) journalAssociation.get("journal");
			this.journal_uuid = (String) journaluuid.get("uuid");
		}
		
//		System.out.println("journal_uuid: " + journal_uuid);
	}

	private String doi;

	@JsonProperty("electronicVersions")
	private void unpackDoi(ArrayList electronicVersionsList) {
//System.out.println("electronicVersionsList length: " + electronicVersionsList.size());
		for (int i = 0; i < electronicVersionsList.size(); i++) {
			Map<String, String> electronicVersionsItem = (Map) (electronicVersionsList.get(i));
			if (electronicVersionsItem.keySet().contains("doi")) {
				this.doi = electronicVersionsItem.get("doi");
			}
//			if (electronicVersionsItem.keySet().contains("file")) {
////				System.out.println("file url: " + electronicVersionsItem.get("file"));
//			}

		}
	}

	private Date embargo_expiry_date;

//	@JsonProperty("embargoEndDate")
//	private void formateDate(String embargoEndDate) throws ParseException {
//
//		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ENGLISH);
//
//		this.embargo_expiry_date = formatter.parse(embargoEndDate);
//
//	}


// just internal author needed, there are more than one author for a purerecord. 	
	private List<AuthorResponse> authors = new ArrayList<>();

	@JsonProperty("personAssociations")
	private void unpackAuthor(ArrayList personAssociationsList) {

//		System.out.println("purerecord.java: length" + personAssociationsList.size());

		for (int i = 0; i < personAssociationsList.size(); i++) {

			AuthorResponse authornew = new AuthorResponse();
			String uuidTemp = "";
			JSONObject jsonObject = new JSONObject();
			Map<String, Object> personAssociationItem = (Map) (personAssociationsList.get(i));

			if (personAssociationItem.keySet().contains("person")) {
				Map<String, Object> uuid = (Map) (personAssociationItem.get("person"));
				uuidTemp = uuid.get("uuid").toString();
				Map<String, Object> name = (Map) (personAssociationItem.get("name"));
				String organisationID = "";
				if (personAssociationItem.keySet().contains("organisationalUnits")) {
					ArrayList organisationalUnits = (ArrayList) personAssociationItem.get("organisationalUnits");
					Map<String, String> organisationalUnit = (Map) organisationalUnits.get(0);
					if(organisationalUnit.keySet().contains("externalId")) {
						organisationID = (String)organisationalUnit.get("externalId");
					}
					
				}

				authornew.setUuid(uuidTemp);
				authornew.setSurname((String)name.get("lastName"));
				
				authornew.setForename((String)name.get("firstName"));
				authornew.setOrganisationID(organisationID);

				this.authors.add(authornew);
//				System.out.println("purerecord.java: authors list" + this.authors);
			}

		}
		// this.authors = array.toString();
	}

	// pure record creaded user and date.
	private String createdBy;
	private Date createdDate;
	private String portalUrl;
	private Date pure_last_modified_date;

	@JsonProperty("info")
	private void unpackInfo(Map<String, Object> info) throws ParseException {
		this.createdBy = (String)info.get("createdBy");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ENGLISH);
		this.createdDate = formatter.parse((String) info.get("createdDate"));
		this.portalUrl = (String)info.get("portalUrl");
		
		if(info.keySet().contains("modifiedDate")) {
			this.pure_last_modified_date = formatter.parse((String) info.get("modifiedDate"));
		}
	}

	private String publisherName;
//	@JsonProperty("publisher")
//	private void unpackPublisher(Map<String, Object> publisher) {
//		
//		Map<String, Object> publishername = (Map)(publisher.get("name"));
//		ArrayList publisherText = (ArrayList)publishername.get("text");
//		Map<String, String> typeTermTextMap = (Map)publisherText.get(0);
//		this.publisherName = typeTermTextMap.get("value");
//		
//	}
	
	private String notes;
	private String embargoPeriod;
	private boolean ukri_S_Flag;
	private boolean ref_compliance_exception;
	private boolean help_raise_visibility;
	private boolean data_access_statement;
	private boolean rights_retention_statement;
	
	private boolean potentially_published;
	private boolean requested_press_release;
	
	
	private String deposit_route;
	private String record_status;
	private String gateway_depositor;
	private String compliance_status;
	
	private String funders;

	private String fileUrl;

	private String ukri_compliance_status;
	private String embargo_length;
	
	private String potentially_doi;

//fields automatically generates 	

	private Date oacp_createdDate;
	private String oacp_createdBy;

	private Date oacp_modifiedDate;
	private String oacp_modifiedBy;

	private Date oacp_syncedDate;
	private String oacp_syncedBy;

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}


	public void setTitle(String title) {
		this.title = title;
	}

	public void setOutputType(String outputType) {
		this.outputType = outputType;
	}

	public void setPublicationStatus(String publicationStatus) {
		this.publicationStatus = publicationStatus;
	}


	public void setJournal(String journal) {
		this.journal = journal;
	}

	public void setDoi(String doi) {
		this.doi = doi;
	}

	public String getPureId() {
		return pureId;
	}

	public void setPureId(String pureId) {
		this.pureId = pureId;
	}

	public String getTitle() {
		return title;
	}

	public String getOutputType() {
		return outputType;
	}

	public String getPublicationStatus() {
		return publicationStatus;
	}

	
	public Date getAcceptedDate() {
		return acceptedDate;
	}

	public void setAcceptedDate(Date acceptedDate) {
		this.acceptedDate = acceptedDate;
	}

	public Date getPublicationDate() {
		return publicationDate;
	}

	public void setPublicationDate(Date publicationDate) {
		this.publicationDate = publicationDate;
	}

	public Date getePublicationDate() {
		return ePublicationDate;
	}

	public void setePublicationDate(Date ePublicationDate) {
		this.ePublicationDate = ePublicationDate;
	}

	public String getJournal() {
		return journal;
	}

	public String getDoi() {
		return doi;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getPublisherName() {
		return publisherName;
	}

	public void setPublisherName(String publisherName) {
		this.publisherName = publisherName;
	}

	public String getPortalUrl() {
		return portalUrl;
	}

	public void setPortalUrl(String portalUrl) {
		this.portalUrl = portalUrl;
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public String getEmbargoPeriod() {
		return embargoPeriod;
	}

	public void setEmbargoPeriod(String embargoPeriod) {
		this.embargoPeriod = embargoPeriod;
	}

	public String getJournal_uuid() {
		return journal_uuid;
	}

	public void setJournal_uuid(String journal_uuid) {
		this.journal_uuid = journal_uuid;
	}

	public List<AuthorResponse> getAuthors() {
		return authors;
	}

	public void setAuthors(List<AuthorResponse> authors) {
		this.authors = authors;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	

	public Date getEmbargo_expiry_date() {
		return embargo_expiry_date;
	}

	public void setEmbargo_expiry_date(Date embargo_expiry_date) {
		this.embargo_expiry_date = embargo_expiry_date;
	}

	

	public String getDeposit_route() {
		return deposit_route;
	}

	public void setDeposit_route(String deposit_route) {
		this.deposit_route = deposit_route;
	}

	public String getRecord_status() {
		return record_status;
	}

	public void setRecord_status(String record_status) {
		this.record_status = record_status;
	}

	public String getGateway_depositor() {
		return gateway_depositor;
	}

	public void setGateway_depositor(String gateway_depositor) {
		this.gateway_depositor = gateway_depositor;
	}

	public String getCompliance_status() {
		return compliance_status;
	}

	public void setCompliance_status(String compliance_status) {
		this.compliance_status = compliance_status;
	}


	public String getFunders() {
		return funders;
	}

	public void setFunders(String funders) {
		this.funders = funders;
	}


	public String getEmbargo_length() {
		return embargo_length;
	}

	public void setEmbargo_length(String embargo_length) {
		this.embargo_length = embargo_length;
	}


	public String getOacp_createdBy() {
		return oacp_createdBy;
	}

	public void setOacp_createdBy(String oacp_createdBy) {
		this.oacp_createdBy = oacp_createdBy;
	}


	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getOacp_createdDate() {
		return oacp_createdDate;
	}

	public void setOacp_createdDate(Date oacp_createdDate) {
		this.oacp_createdDate = oacp_createdDate;
	}

	public Date getOacp_modifiedDate() {
		return oacp_modifiedDate;
	}

	public void setOacp_modifiedDate(Date oacp_modifiedDate) {
		this.oacp_modifiedDate = oacp_modifiedDate;
	}

	public Date getOacp_syncedDate() {
		return oacp_syncedDate;
	}

	public void setOacp_syncedDate(Date oacp_syncedDate) {
		this.oacp_syncedDate = oacp_syncedDate;
	}

	public String getOacp_modifiedBy() {
		return oacp_modifiedBy;
	}

	public void setOacp_modifiedBy(String oacp_modifiedBy) {
		this.oacp_modifiedBy = oacp_modifiedBy;
	}

	public String getOacp_syncedBy() {
		return oacp_syncedBy;
	}

	public void setOacp_syncedBy(String oacp_syncedBy) {
		this.oacp_syncedBy = oacp_syncedBy;
	}

	 
	public Date getPure_last_modified_date() {
		return pure_last_modified_date;
	}

	public void setPure_last_modified_date(Date pure_last_modified_date) {
		this.pure_last_modified_date = pure_last_modified_date;
	}

	
	public boolean isUkri_S_Flag() {
		return ukri_S_Flag;
	}

	public void setUkri_S_Flag(boolean ukri_S_Flag) {
		this.ukri_S_Flag = ukri_S_Flag;
	}

	public String getUkri_compliance_status() {
		return ukri_compliance_status;
	}

	public void setUkri_compliance_status(String ukri_compliance_status) {
		this.ukri_compliance_status = ukri_compliance_status;
	}

	public boolean isPotentially_published() {
		return potentially_published;
	}

	public void setPotentially_published(boolean potentially_published) {
		this.potentially_published = potentially_published;
	}

	public boolean isRequested_press_release() {
		return requested_press_release;
	}

	public void setRequested_press_release(boolean requested_press_release) {
		this.requested_press_release = requested_press_release;
	}

	public boolean isHelp_raise_visibility() {
		return help_raise_visibility;
	}

	public void setHelp_raise_visibility(boolean help_raise_visibility) {
		this.help_raise_visibility = help_raise_visibility;
	}

	public boolean isData_access_statement() {
		return data_access_statement;
	}

	public void setData_access_statement(boolean data_access_statement) {
		this.data_access_statement = data_access_statement;
	}

	public boolean isRights_retention_statement() {
		return rights_retention_statement;
	}

	public void setRights_retention_statement(boolean rights_retention_statement) {
		this.rights_retention_statement = rights_retention_statement;
	}

	public boolean isRef_compliance_exception() {
		return ref_compliance_exception;
	}

	public void setRef_compliance_exception(boolean ref_compliance_exception) {
		this.ref_compliance_exception = ref_compliance_exception;
	}

	public String getPotentially_doi() {
		return potentially_doi;
	}

	public void setPotentially_doi(String potentially_doi) {
		this.potentially_doi = potentially_doi;
	}
	
	

}
