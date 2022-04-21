package uk.ac.man.library.oacpv2.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import uk.ac.man.library.oacpv2.model.Publication;



@Repository
public interface PublicationRepository extends JpaRepository<Publication, String> {
	
	Page<Publication> findAll(Pageable pageable);

	Publication findBypureId(String pureId);

	List<Publication> findByPureIdContaining(String pureId);

	@Query("SELECT pureId FROM Publication")
	List<String> getAllPureID();
	
	@Query("SELECT pureId FROM Publication where oacp_createdDate >= :creationDateTime")
	List<String> getRecordWithin2Months(@Param("creationDateTime") Date creationDateTime);
	
	
	@Query("SELECT p FROM Publication p where p.note_temp is not null")
	List<Publication> getByNote();

	@Query("SELECT DISTINCT p FROM Publication p " 
			+ "LEFT JOIN p.authors a " 
			+ "WHERE (:pureId='' or p.pureId IN :pureId) "
			+ "AND (:publicationStatus='' or p.publicationStatus IN :publicationStatus) "
			+ "AND (:record_status='' or p.record_status IN :record_status) "
			+ "AND (p.potentially_published IN :potentially_published) "
			+ "AND (p.ukri_S_Flag IN :ukri_S_Flag) "
			+ "AND (p.help_raise_visibility IN :help_raise_visibility) "
			+ "AND (:compliance_status='' or p.compliance_status IN :compliance_status) "
			+ "AND (:ukri_compliance_status='' or p.ukri_compliance_status IN :ukri_compliance_status) "
			+ "AND (:doi='' or p.doi IN :doi) "
			+ "AND (:school_id='' or a.school_id IN :school_id) "
			+ "AND (:faculty_id='' or a.faculty_id IN :faculty_id)"
			)
	Page<Publication> PublicationFiler(@Param("pureId")String pureId, 
			@Param("publicationStatus")String publicationStatus, 
			@Param("record_status")String record_status,
			@Param("compliance_status")String compliance_status, 
			@Param("ukri_compliance_status")String ukri_compliance_status, 
			@Param("potentially_published") Collection<Boolean> potentially_published, 
			@Param("ukri_S_Flag") Collection<Boolean> ukri_S_Flag, 
			@Param("help_raise_visibility") Collection<Boolean> help_raise_visibility, 
			@Param("doi")String doi,
			@Param("school_id")String school_id, 
			@Param("faculty_id")String faculty_id,
			Pageable pageable);
	
	
	
	@Query("SELECT DISTINCT p FROM Publication p " 
			+ "LEFT JOIN p.authors a " 
			+ "WHERE (:pureId='' or p.pureId IN :pureId) "
			+ "AND (:publicationStatus='' or p.publicationStatus IN :publicationStatus) "
			+ "AND (:record_status='' or p.record_status IN :record_status) "
			+ "AND (p.potentially_published IN :potentially_published) "
			+ "AND (p.ukri_S_Flag IN :ukri_S_Flag) "
			+ "AND (p.help_raise_visibility IN :help_raise_visibility) "
			+ "AND (:compliance_status='' or p.compliance_status IN :compliance_status) "
			+ "AND (:ukri_compliance_status='' or p.ukri_compliance_status IN :ukri_compliance_status) "
			+ "AND (:doi='' or p.doi IN :doi) "
			+ "AND (:school_id='' or a.school_id IN :school_id) "
			+ "AND (:faculty_id='' or a.faculty_id IN :faculty_id)"
			)
	List<Publication> ExportByPureid(@Param("pureId")String pureId, 
			@Param("publicationStatus")String publicationStatus, 
			@Param("record_status")String record_status,
			@Param("compliance_status")String compliance_status, 
			@Param("ukri_compliance_status")String ukri_compliance_status, 
			@Param("potentially_published") Collection<Boolean> potentially_published, 
			@Param("ukri_S_Flag") Collection<Boolean> ukri_S_Flag, 
			@Param("help_raise_visibility") Collection<Boolean> help_raise_visibility, 
			@Param("doi")String doi,
			@Param("school_id")String school_id, 
			@Param("faculty_id")String faculty_id);
	
	@Query("SELECT p FROM Publication p " + "WHERE p.record_status LIKE %:recordStatus% "
			+ "AND p.potentially_published=false")
	List<Publication> PublicationWaitingToBePublished(@Param("recordStatus") String recordStatus);
	
}
