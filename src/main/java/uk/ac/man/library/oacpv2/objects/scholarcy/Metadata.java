package uk.ac.man.library.oacpv2.objects.scholarcy;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Metadata {

	@JsonProperty("title")
	private String title;

	@JsonProperty("author")
	private String author;

	@JsonProperty("pages")
	private String pages;

	@JsonProperty("date")
	private String date;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getPages() {
		return pages;
	}

	public void setPages(String pages) {
		this.pages = pages;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}


}
