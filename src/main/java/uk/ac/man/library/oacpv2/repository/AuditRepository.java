package uk.ac.man.library.oacpv2.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import uk.ac.man.library.oacpv2.model.Audit;

public interface AuditRepository extends CrudRepository<Audit, Long> {
	
	@Query("SELECT a FROM Audit a " 
			+ "WHERE (:message='' or a.message LIKE %:message%) "
			+ "AND (:level='' or a.level = :level) "
			+ "AND (:type='' or a.type = :type) "
			)
	Page<Audit> findAll(
			@Param("message")String message, 
			@Param("level")String level, 
			@Param("type")String type,
			Pageable pageable);
}
