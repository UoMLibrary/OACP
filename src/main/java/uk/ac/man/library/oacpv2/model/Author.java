package uk.ac.man.library.oacpv2.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Author {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	private String uuid;

	private String forename;

	private String surname;

	private String spotID;

	private String emailAddress;

	private String school_name;
	private String school_id;

	private String faculty_name;
	private String faculty_id;

	@ManyToMany(mappedBy = "authors")
	@JsonIgnore
	private Set<Publication> publications = new HashSet<>();
	
	

	public Set<Publication> getPublications() {
		return publications;
	}

	public void setPublications(Set<Publication> publications) {
		this.publications = publications;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getForename() {
		return forename;
	}

	public void setForename(String forename) {
		this.forename = forename;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getSpotID() {
		return spotID;
	}

	public void setSpotID(String spotID) {
		this.spotID = spotID;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getSchool_name() {
		return school_name;
	}

	public void setSchool_name(String school_name) {
		this.school_name = school_name;
	}

	public String getFaculty_name() {
		return faculty_name;
	}

	public void setFaculty_name(String faculty_name) {
		this.faculty_name = faculty_name;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getSchool_id() {
		return school_id;
	}

	public void setSchool_id(String school_id) {
		this.school_id = school_id;
	}

	public String getFaculty_id() {
		return faculty_id;
	}

	public void setFaculty_id(String faculty_id) {
		this.faculty_id = faculty_id;
	}

}
