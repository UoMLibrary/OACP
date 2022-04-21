package uk.ac.man.library.oacpv2.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.persistence.EntityManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import uk.ac.man.library.oacpv2.model.Author;
import uk.ac.man.library.oacpv2.controller.PureController;
import uk.ac.man.library.oacpv2.model.*;

@Repository
public class JdbcPublicationRepImple implements JdbcPublicationRepository{

	private static final Logger logger = LoggerFactory.getLogger(JdbcPublicationRepImple.class.getName());
	
	
	
	@Autowired
    private JdbcTemplate jdbcTemplate;
	
	
    @Override
    public List<Publication> PublicationFiler(String pureId, String publicationStatus, String record_status,
    		String help_raise_visibility, String requested_press_release, String compliance_status, String doi,
			String school_id, String faculty_id){
//    	
    	String Sql = "select distinct on (pu.pure_id) pu.* from publication as pu "
    	+"join publication_authors as pa on (pu._id = pa.publications__id) "
    	+"join author as au on (pa.authors_id=au.id) ";
    	
    	
    	if(pureId != "") {
    		Sql += "where pu.pure_id like '" + pureId +"'";
    	}
    	
    	
    	logger.debug("SQL:" +Sql);
    	
//    	
//    	List<response> listPu = jdbcTemplate.query(Sql, 
//    			new BeanPropertyRowMapper(response.class));
    	
    	List<Publication> listPu = jdbcTemplate.query(Sql, 
    			new BeanPropertyRowMapper(Publication.class));
    	
    	return listPu;
//    	List<Author> auth = jdbcTemplate.query(Sql, 
//    			new BeanPropertyRowMapper(Author.class));
 
//    	return jdbcTemplate.query(Sql, new ResultSetExtractor<List<Publication> >() {
//    		
//    		@Override
//    		public List<Publication> extractData(ResultSet rs) throws SQLException, DataAccessException {
//    			List<Publication> list = new ArrayList<Publication>();
//    			Map<String, Publication> publicationMap = new HashMap<String, Publication>();
//    			Map<Long, Author> auathorMap = new HashMap<Long, Author>();
//    			
//    			while (rs.next()) {
//    				
//    				String publication_id = rs.getString("_id");
//    				Publication pub = publicationMap.get(publication_id);
//    				if(pub == null) {
//    					pub = new Publication();
//    					list.add(pub);
//    					
//    					pub.setId(publication_id);
//    					pub.setPureId(rs.getString("pure_id"));
//    					pub.setTitle(rs.getString("title"));
//    					
//    				}
//    				
//    				System.out.println("column:" + rs.findColumn("id"));
//    				Long authorId = rs.getLong("id");
//    				Author auth = auathorMap.get(authorId);
//    				if(auth ==  null) {
//    					auth = new Author();
//    					
//    					if(pub.getAuthors() == null) {
//    						pub.setAuthors(new HashSet<Author>());
//    					}
//    					pub.getAuthors().add(auth);
//    					auathorMap.put(authorId, auth);
//    					
//    					auth.setEmailAddress(rs.getString("email_address"));
//    					
//    				}
//    			}
//    			
//    			return list;
//    		
//    }
//    	});
    
}
    
    
}
