package uk.ac.man.library.oacpv2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import uk.ac.man.library.oacpv2.model.Author;
import uk.ac.man.library.oacpv2.repository.AuthorRepository;

@RestController
public class AuthorController{
	
	@Autowired
	private AuthorRepository authorRepository;

	@GetMapping(path="/author/all")
	public Iterable<Author> getAuthors() {
		return authorRepository.findAll();
		
	}
}
