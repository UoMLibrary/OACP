package uk.ac.man.library.oacpv2.objects.scholarcy;

import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonProperty;

public class KnowledgeResponse {

	@JsonProperty("filename")
	private String filename;

	@JsonProperty("highlights")
	private ArrayList<String> highlights;

	@JsonProperty("headline")
	private String headline;

	@JsonProperty("key_statements")
	private ArrayList<String> key_statements;

	@JsonProperty("metadata")
	private Metadata metadata;

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public ArrayList<String> getHighlights() {
		return highlights;
	}

	public void setHighlights(ArrayList<String> highlights) {
		this.highlights = highlights;
	}

	public String getHeadline() {
		return headline;
	}

	public void setHeadline(String headline) {
		this.headline = headline;
	}

	public ArrayList<String> getKey_statements() {
		return key_statements;
	}

	public void setKey_statements(ArrayList<String> key_statements) {
		this.key_statements = key_statements;
	}

	public Metadata getMetadata() {
		return metadata;
	}

	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}


}
