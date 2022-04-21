package uk.ac.man.library.oacpv2.repository;

import java.util.List;

import uk.ac.man.library.oacpv2.model.Author;
import uk.ac.man.library.oacpv2.model.*;



public interface JdbcPublicationRepository {
	
	List<Publication> PublicationFiler(String pureId, String publicationStatus, String record_status,
			String help_raise_visibility, String requested_press_release, String compliance_status, String doi,
			String school_id, String faculty_id);

}
