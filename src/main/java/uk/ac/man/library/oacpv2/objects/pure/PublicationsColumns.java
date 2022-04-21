package uk.ac.man.library.oacpv2.objects.pure;

import java.util.Date;

public class PublicationsColumns {
	private String pureId="";
	private String publicationStatus="";
	private String title="";
	private String record_status="";
	private String compliance_status="";
	private String ukri_compliance_status="";
	private String acceptedDate;
	private String ePublicationDate;
	private String publicationDate;
	private String potentially_published;
	
	
	
	public String getPureId() {
		return pureId;
	}
	public void setPureId(String pureId) {
		this.pureId = pureId;
	}
	public String getPublicationStatus() {
		return publicationStatus;
	}
	public void setPublicationStatus(String publicationStatus) {
		this.publicationStatus = publicationStatus;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getRecord_status() {
		return record_status;
	}
	public void setRecord_status(String record_status) {
		this.record_status = record_status;
	}
	public String getCompliance_status() {
		return compliance_status;
	}
	public void setCompliance_status(String compliance_status) {
		this.compliance_status = compliance_status;
	}
	
	public String getUkri_compliance_status() {
		return ukri_compliance_status;
	}
	public void setUkri_compliance_status(String ukri_compliance_status) {
		this.ukri_compliance_status = ukri_compliance_status;
	}
	public String getAcceptedDate() {
		return acceptedDate;
	}
	public void setAcceptedDate(String acceptedDate) {
		this.acceptedDate = acceptedDate;
	}
	public String getePublicationDate() {
		return ePublicationDate;
	}
	public void setePublicationDate(String ePublicationDate) {
		this.ePublicationDate = ePublicationDate;
	}
	public String getPublicationDate() {
		return publicationDate;
	}
	public void setPublicationDate(String publicationDate) {
		this.publicationDate = publicationDate;
	}
	public String getPotentially_published() {
		return potentially_published;
	}
	public void setPotentially_published(String potentially_published) {
		this.potentially_published = potentially_published;
	}
	
}
