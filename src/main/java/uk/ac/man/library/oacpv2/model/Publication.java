package uk.ac.man.library.oacpv2.model;

import java.util.Date;
import java.util.List;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;


@Entity
@Table(name = "Publication")
public class Publication {
	
	@Id
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	@Column(name = "_id", length = 40, unique=true)
	private String id;

//fields from pure api	
	@Column(name = "pure_id")
	private String pureId;
	@Column(name = "title", length = 2048)
	private String title;
	@Column(name = "output_type")
	private String outputType;
	@Column(name = "pure_status")
	private String publicationStatus;
	@Column(name = "accepted_date")
	private Date acceptedDate;
	@Column(name = "published_date")
	private Date publicationDate;
	@Column(name = "e_publication_date")
	private Date ePublicationDate;
	private String journal;
	private String doi;
	private String potentially_doi;
	@Column(name = "pure_record_creator")
	private String createdBy;
	@Column(name = "pure_record_created_date")
	private Date createdDate;
	private String publisherName;
	@Column(name = "portalUrl", length = 2048)
	private String portalUrl;
	
	private Date embargo_expiry_date;
	

	private boolean potentially_published;
	
	@Column(name = "pure_last_modified_date")
	private Date pure_last_modified_date;
	
	//this is temporary column for migration purpose.
	@Column(name = "record_last_modified_date")
	private String record_last_modified_date;
	
//fields add by OACP app.
	private boolean ukri_S_Flag;
	
	private String deposit_route;
	private String record_status;
	private String gateway_depositor;
	private String compliance_status;
	private boolean requested_press_release;
	private boolean help_raise_visibility;
	private String funders;
	@Column(name = "fielUrl", length = 2048)
	private String fileUrl;
	private boolean ref_compliance_exception;
	
	private String ukri_compliance_status;
	private String embargo_length;
	private boolean data_access_statement;
	private boolean rights_retention_statement;

//fields automatically generates 
	@Column(name = "created_by")
	private String oacp_createdBy;
	@Column(name = "created_date")
	private Date oacp_createdDate;
	
	@Column(name = "last_modified_by")
	private String oacp_modifiedBy;
	@Column(name = "last_modified_date")
	private Date oacp_modifiedDate;
	
	@Column(name = "last_synced_by")
	private String oacp_syncedBy;
	@Column(name = "last_synced_date")
	private Date oacp_syncedDate;

//fields form pure api	
	@ManyToMany
	private Set<Author> authors = new HashSet<>();
	
	@OneToMany(mappedBy="publication")
	private List<Note> note_list;
	
	
	//temporary notes column for migrate notes
	@Column(name = "notes", length = 2048)
	private String note_temp;
	
	
	
	public List<Note> getNote_list() {
		return note_list;
	}

	public void setNote_list(List<Note> note_list) {
		this.note_list = note_list;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getOutputType() {
		return outputType;
	}

	public void setOutputType(String outputType) {
		this.outputType = outputType;
	}

	public String getPublicationStatus() {
		return publicationStatus;
	}

	public void setPublicationStatus(String publicationStatus) {
		this.publicationStatus = publicationStatus;
	}


	public String getJournal() {
		return journal;
	}

	public void setJournal(String journal) {
		this.journal = journal;
	}

	public String getDoi() {
		return doi;
	}

	public void setDoi(String doi) {
		this.doi = doi;
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public String getPureId() {
		return pureId;
	}

	public void setPureId(String pureId) {
		this.pureId = pureId;
	}

	
	public Set<Author> getAuthors() {
		return authors;
	}

	public void setAuthors(Set<Author> authors) {
		this.authors = authors;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
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

	public String getOacp_createdBy() {
		return oacp_createdBy;
	}

	public void setOacp_createdBy(String oacp_createdBy) {
		this.oacp_createdBy = oacp_createdBy;
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


	
	public String getUkri_compliance_status() {
		return ukri_compliance_status;
	}

	public void setUkri_compliance_status(String ukri_compliance_status) {
		this.ukri_compliance_status = ukri_compliance_status;
	}

	public String getEmbargo_length() {
		return embargo_length;
	}

	public void setEmbargo_length(String embargo_length) {
		this.embargo_length = embargo_length;
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

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getEmbargo_expiry_date() {
		return embargo_expiry_date;
	}

	public void setEmbargo_expiry_date(Date embargo_expiry_date) {
		this.embargo_expiry_date = embargo_expiry_date;
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

	
	public boolean isUkri_S_Flag() {
		return ukri_S_Flag;
	}

	public void setUkri_S_Flag(boolean ukri_S_Flag) {
		this.ukri_S_Flag = ukri_S_Flag;
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

	public String getNote_temp() {
		return note_temp;
	}

	public void setNote_temp(String note_temp) {
		this.note_temp = note_temp;
	}

	public Date getPure_last_modified_date() {
		return pure_last_modified_date;
	}

	public void setPure_last_modified_date(Date pure_last_modified_date) {
		this.pure_last_modified_date = pure_last_modified_date;
	}

	public String getRecord_last_modified_date() {
		return record_last_modified_date;
	}

	public void setRecord_last_modified_date(String record_last_modified_date) {
		this.record_last_modified_date = record_last_modified_date;
	}

	
	public boolean isRef_compliance_exception() {
		return ref_compliance_exception;
	}

	public void setRef_compliance_exception(boolean ref_compliance_exception) {
		this.ref_compliance_exception = ref_compliance_exception;
	}

	public Publication() {
		super();
	}

	public void addAuthor(Author author) {
		this.authors.add(author);
		author.getPublications().add(this);
	}
	
	
	public void removeAuthor(Long authorId) {
		Author author = this.authors.stream().filter(t -> t.getId() == authorId).findFirst().orElse(null);
		if(author != null) this.authors.remove(author);
		
		author.getPublications().remove(this);
	}

	public String getPotentially_doi() {
		return potentially_doi;
	}

	public void setPotentially_doi(String potentially_doi) {
		this.potentially_doi = potentially_doi;
	}
	
	
	
	
}
