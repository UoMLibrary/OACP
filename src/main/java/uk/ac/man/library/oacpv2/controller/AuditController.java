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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.man.library.oacpv2.model.Audit;
import uk.ac.man.library.oacpv2.model.User;
import uk.ac.man.library.oacpv2.objects.pure.AuditsDTO;
import uk.ac.man.library.oacpv2.repository.AuditRepository;

@SessionAttributes("audits")
@Controller
public class AuditController {
	
	
	private static final Logger logger = LoggerFactory.getLogger(AuditController.class.getName());

	@Autowired
	private AuditRepository auditRepository;
	
	
	
	@GetMapping(path = "/audit/all")
	public ModelAndView getauditView(ModelAndView modelAndView, HttpServletRequest request) {
		
		
		HttpSession session = request.getSession(false);
		if (session != null){
			if (session.getAttribute("isValid") != null){
				User user = (User) session.getAttribute("user");

				logger.info("/audit/all, logged username: " + user);
				modelAndView.addObject("user", user);
				
				
				modelAndView.setViewName("audit/list_audit");
				
			}else {
				logger.info("No Valid");
				modelAndView.setViewName("redirect:/");
			}
				
		}else {
			//end if session null
			logger.info("No session");
			modelAndView.setViewName("redirect:/");
		}
		

		return modelAndView;

	}
	
	
	@GetMapping(path = "/audit/list")
	@ResponseBody
	public AuditsDTO getAuditAll(
			HttpServletRequest request,
			@RequestParam("start") int start,
			@RequestParam("length") int length,
			@RequestParam("draw") int draw) {
		//check session for authentication
		HttpSession session = request.getSession(false);
		if(session != null) {
			if(session.getAttribute("isValid") == "yes") {
				User user = (User) session.getAttribute("user");
				logger.info("pure/create_record, logged username: " + user.getFullName());
				
				//search parameters
				String message = "";
				String type = "";
				String level = "";
				
				if(request.getParameter("message") != null) {
					message = request.getParameter("message");
				}
				
				if(request.getParameter("type") != null) {
					type = request.getParameter("type");
				}
				
				if(request.getParameter("level") != null) {
					level = request.getParameter("level");
				}
				
				//pagination and sorting
				String orderstr = request.getParameter("order[0][dir]");
				String orderNo = request.getParameter("order[0][column]");
				String ordercol = "columns["+orderNo+"][data]";
				String orderColumn = request.getParameter(ordercol);
				

				Order order = new Order(getSortDirection(orderstr), orderColumn);
				int page = start/length;
				Pageable pageable = PageRequest.of(page, length,Sort.by(order));
				
				AuditsDTO auditDto = new AuditsDTO();
				List<Audit> auditList = new ArrayList<>();
				Page<Audit> auditPage =  auditRepository.findAll(message,level,type,pageable);
				
				for(Audit audit:auditPage) {
					auditList.add(audit);
				}
				auditDto.setData(auditList);
				auditDto.setDraw(draw);
				auditDto.setRecordsFiltered(auditPage.getTotalElements());
				auditDto.setRecordsTotal(auditPage.getTotalElements());
				
				return auditDto;
			}else {
				logger.info("No valid");
				return null;
			}
		}else {
			logger.info("No session");
			return null;
		}
		
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
