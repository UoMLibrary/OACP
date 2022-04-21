package uk.ac.man.library.oacpv2.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import uk.ac.man.library.oacpv2.model.Author;


public interface AuthorRepository extends CrudRepository<Author, Long>{
	
	List<Author> findByUuid(String uuid);

}
