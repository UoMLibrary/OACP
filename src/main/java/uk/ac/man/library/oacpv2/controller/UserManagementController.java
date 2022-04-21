package uk.ac.man.library.oacpv2.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.man.library.oacpv2.model.User;
import uk.ac.man.library.oacpv2.objects.pure.UserDTO;
import uk.ac.man.library.oacpv2.objects.pure.publicationsDTO;
import uk.ac.man.library.oacpv2.repository.UserManagementRepository;

@SessionAttributes("userRecord")
@Controller
public class UserManagementController {

	private static final Logger logger = LoggerFactory.getLogger(UserManagementController.class.getName());

	private final UserManagementRepository userManagementRepository;

	@Autowired
	public UserManagementController(UserManagementRepository userManagementRepository) {
		this.userManagementRepository = userManagementRepository;
	}


	@PostMapping(path = "/users/addUsers")
	@ResponseBody
	public String createUser(User userRecord) {
		String userName = userRecord.getUserName();
		String responseMes = "";
		User user = new User();
		try {
			user = userManagementRepository.findByUserName(userName);
		}catch(Exception e) {
			logger.error("add user: " + e);
		}
		if(user==null) {
			userManagementRepository.save(userRecord);
			responseMes = "sucess";
		}else {
			responseMes =  "exist";
		}
		
		
		return responseMes;
	}
	
	@PostMapping(path = "/users/updateUsers")
	@ResponseBody
	public String updateUser(User userRecord,HttpServletRequest request) {
		
		User user = userManagementRepository.findByuserId(userRecord.getUserId());
		user.setFullName(userRecord.getFullName());
		user.setRole(userRecord.getRole());
		user.setUserName(userRecord.getUserName());
		userManagementRepository.save(user);
		
		return "update";

	}


	@GetMapping("/users")
	public ModelAndView getUser(HttpServletRequest request,
			User userRecord) {
		
		ModelAndView modelAndView = new ModelAndView();
		HttpSession session = request.getSession(false);
		if(session != null) {
		if(session.getAttribute("isValid") == "yes") {
				User user = (User) session.getAttribute("user");
				logger.info("user list, logged username: " + user.getFullName());
				modelAndView.addObject("user", user);	
			// proceed.......... main code
				
				modelAndView.addObject("userRecord", userRecord);
				modelAndView.setViewName("user/create_user");
			}else {
				logger.info("No valid");
				modelAndView.setViewName("error-pages/403");
			}
		}else {
			logger.info("No session");
			modelAndView.setViewName("redirect:/");
		}
		
		return modelAndView;
		 
	}


	@GetMapping("/users/{userid}")
	public ModelAndView getUserById(@PathVariable("userid") Long userid, HttpServletRequest request) {

		ModelAndView modelAndView = new ModelAndView();
		HttpSession session = request.getSession(false);
		if(session != null) {
		if(session.getAttribute("isValid") == "yes") {
			User loggeduser = (User) session.getAttribute("user");
				logger.info("pure/create_record, logged username: " + loggeduser.getFullName());
				modelAndView.addObject("user", loggeduser);	
			// proceed.......... main code
				User users = userManagementRepository.findByuserId(userid);
				 
					modelAndView.addObject("users", users);
					modelAndView.setViewName("user/edit-user");
			}else {
				logger.info("No valid");
				modelAndView.setViewName("error-pages/403");
			}
		}else {
			logger.info("No session");
			modelAndView.setViewName("redirect:/");
		}

		return modelAndView;
	}

	@GetMapping(path = "/users/list")
	@ResponseBody
	public UserDTO UserList(HttpServletRequest request,
			@RequestParam("start") int start,
			@RequestParam("length") int length,
			@RequestParam("draw") int draw) {
		
		HttpSession session = request.getSession(false);
		if (session != null) {
			if (session.getAttribute("isValid") == "yes") {
				
				
				
				String orderstr = request.getParameter("order[0][dir]");
				String orderNo = request.getParameter("order[0][column]");
				String ordercol = "columns["+orderNo+"][data]";
				String orderColumn = request.getParameter(ordercol);
				

				Order order = new Order(getSortDirection(orderstr), orderColumn);
				int page = start/length;
				Pageable pageable = PageRequest.of(page, length,Sort.by(order));
				
				UserDTO userDto = new UserDTO();
				Page<User> userPage = userManagementRepository.findAll(pageable);
				List<User> userList = new ArrayList<>();
				
				for(User user: userPage) {
					userList.add(user);
				}
				
				userDto.setDraw(draw);
				userDto.setData(userList);
				userDto.setRecordsFiltered(userPage.getTotalElements());
				userDto.setRecordsTotal(userPage.getTotalElements());
				
				return userDto;
			}else {
				logger.info("No Valid");
				return null;
			}
			
		}else {
			logger.info("No session");
			return null;
		}
		
	}
	
	
	@GetMapping(path = "/user")
	public ModelAndView getUsers(ModelAndView modelAndView,
			HttpServletRequest request,
			User userRecord) {
		
		HttpSession session = request.getSession(false);
		if(session != null) {
		if(session.getAttribute("isValid") == "yes") {
				User loggeduser = (User) session.getAttribute("user");
				logger.info("pure/create_record, logged username: " + loggeduser.getFullName());
				modelAndView.addObject("user", loggeduser);	
			// proceed.......... main code
				
				if(session.getAttribute("message") != null) {
					String message = (String) session.getAttribute("message");
					session.removeAttribute("message");
					modelAndView.addObject("message",message);
				}
				
				
				modelAndView.addObject("userRecord", userRecord);
				modelAndView.setViewName("user/manage_user");
			}else {
				logger.info("No valid");
				modelAndView.setViewName("error-pages/403");
			}
		}else {
			logger.info("No session");
			modelAndView.setViewName("redirect:/");
		}
		return modelAndView;

	}



	@GetMapping(path = "/user/delete/{userId}")
	@ResponseBody
	public String deleteUser(@PathVariable("userId") Long userId,
			HttpServletRequest request) {
		String responseMes = "";
		try {
			userManagementRepository.deleteById(userId);
			responseMes =  "delete";
		}catch(Exception e) {
			logger.error("DELETE USER EXCEPTION:"  + e);
		}
		
		return responseMes;

	}
	
	
	

	 private Sort.Direction getSortDirection(String direction) {
		    if (direction.equals("asc")) {
		      return Sort.Direction.ASC;
		    } else if (direction.equals("desc")) {
		      return Sort.Direction.DESC;
		    }
		    return Sort.Direction.ASC;
		  }
	 

}
