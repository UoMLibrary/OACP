package uk.ac.man.library.oacpv2.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import uk.ac.man.library.oacpv2.model.Note;
import uk.ac.man.library.oacpv2.model.Publication;

public interface NoteRepository extends JpaRepository<Note, String>{
	
	List<Note> findByPublication(Publication publication);
	
}
