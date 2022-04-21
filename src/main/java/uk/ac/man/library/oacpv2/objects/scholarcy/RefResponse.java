package uk.ac.man.library.oacpv2.objects.scholarcy;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RefResponse {

	@JsonProperty("status")
	private String status;

	@JsonProperty("message-type")
	private String messagetype;

	@JsonProperty("message-version")
	private String messageVersion;

	@JsonProperty("message")
	private ResponseElements message;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessagetype() {
		return messagetype;
	}

	public void setMessagetype(String messagetype) {
		this.messagetype = messagetype;
	}

	public String getMessageVersion() {
		return messageVersion;
	}

	public void setMessageVersion(String messageVersion) {
		this.messageVersion = messageVersion;
	}

	public ResponseElements getMessage() {
		return message;
	}

	public void setMessage(ResponseElements message) {
		this.message = message;
	}

}
