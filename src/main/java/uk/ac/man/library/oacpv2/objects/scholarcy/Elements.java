package uk.ac.man.library.oacpv2.objects.scholarcy;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Elements {


	@JsonProperty("author")
	private ArrayList<Author> author;

	@JsonProperty("title")
	private ArrayList<String> title;

	@JsonProperty("DOI")
	private String doi;


	public ArrayList<Author> getAuthor() {
		return author;
	}

	public void setAuthor(ArrayList<Author> author) {
		this.author = author;
	}

	public ArrayList<String> getTitle() {
		return title;
	}

	public void setTitle(ArrayList<String> title) {
		this.title = title;
	}

	public String getDoi() {
		return doi;
	}

	public void setDoi(String doi) {
		this.doi = doi;
	}

}
