package uk.ac.man.library.oacpv2.model;


import java.sql.Array;
import java.util.ArrayList;
import java.util.List;


public class PublicationRes {
	
	private String pureId;
	private String title;
	private String outputType;
	
	
	private List<Author> authors;
	
	public List<Author> getAuthors() {
		return authors;
	}
	public void setAuthors(List<Author> authors) {
		this.authors = authors;
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
	public void setTitle(String title) {
		this.title = title;
	}
	public String getOutputType() {
		return outputType;
	}
	public void setOutputType(String outputType) {
		this.outputType = outputType;
	}
	public PublicationRes(String pureId, String title, String outputType) {
		super();
		this.pureId = pureId;
		this.title = title;
		this.outputType = outputType;
		 
	}
 
	
	
}
