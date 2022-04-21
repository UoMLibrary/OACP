package uk.ac.man.library.oacpv2.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import uk.ac.man.library.oacpv2.model.User;

/**
 * 
 * This is the userManaagement Repository for the Users.
 *
 */

public interface UserManagementRepository extends JpaRepository<User, Long> {

	User findByuserId(Long pureId);

	User findByUserName(String userName);
	
	Page<User> findAll(Pageable pageable);

}
