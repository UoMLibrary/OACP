package uk.ac.man.library.oacpv2.model;

import java.util.List;

public class response {
	private List<Publication> publication;
	private List<Author> authors;
	public List<Publication> getPublication() {
		return publication;
	}
	public void setPublication(List<Publication> publication) {
		this.publication = publication;
	}
	public List<Author> getAuthors() {
		return authors;
	}
	public void setAuthors(List<Author> authors) {
		this.authors = authors;
	}
	
	
}
