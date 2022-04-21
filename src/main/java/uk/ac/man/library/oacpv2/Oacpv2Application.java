package uk.ac.man.library.oacpv2;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;

import uk.ac.man.library.oacpv2.controller.PureController;
import uk.ac.man.library.oacpv2.model.User;
import uk.ac.man.library.oacpv2.repository.AuditRepository;
import uk.ac.man.library.oacpv2.repository.UserManagementRepository;

@Controller
@SpringBootApplication
@EnableScheduling
public class Oacpv2Application extends SpringBootServletInitializer {

	private static final Logger logger = LoggerFactory.getLogger(Oacpv2Application.class.getName());

	public static void main(String[] args) {
		SpringApplication.run(Oacpv2Application.class, args);
	}

	@Autowired
	private UserManagementRepository userRepository;
	
	
	@Value("${casLoginUrl}")
	String casLoginUrl;
	
	@Value("${casLogoutUrl}")
	String casLogoutUrl;

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Oacpv2Application.class);
	}

	@RequestMapping("/")
	public ModelAndView LoadAppplication(HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView();
//		 Uncomment to work on local machine
//		  HttpSession session = request.getSession(true);
//		  
//		  User user = new User();
//		  user.setFullName("localUser");
//		  user.setRole("Admin");
//		  
//		  session.setAttribute("user", user);
//		  session.setAttribute("isValid", "yes"); 
//		  modelAndView.addObject("user", user);
//		  modelAndView.setViewName("pure/index");
		
//		// end Uncomment to work on local machine	
		
		
//		 Start commenting here to work locally
		if (request.getParameter("ticket") == null) {
			// forward to CAS server for authentication
			String redirectURL = casLoginUrl + "?service=" + getCurrentUrl(request);
			
			logger.info("bo ticket, redirect to login:" + redirectURL);
			modelAndView.setViewName("redirect:" + redirectURL);
			return modelAndView;
		}

		try {

			AttributePrincipal principal = null;
			Cas20ServiceTicketValidator sv = new Cas20ServiceTicketValidator(casLoginUrl);

			Assertion a = sv.validate(request.getParameter("ticket"), getCurrentUrl(request));
			principal = a.getPrincipal();
			HttpSession session = request.getSession(true);
			String userName = principal.getName();
			logger.info("userName=" + userName);

			User user = userRepository.findByUserName(userName);
			if (user != null) {
				
				session.setAttribute("user", user);
				session.setAttribute("isValid", "yes");
				logger.info("OK logged in");
				modelAndView.addObject("user", user);
				modelAndView.setViewName("pure/index");
			} else {
				logger.error("no user founded");
				modelAndView.addObject("error", "invalid authentication");
				modelAndView.setViewName("error-pages/401");
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("login error" + e);
			modelAndView.setViewName("redirect:/");
		}
//	 End commenting here to work locally
 
		return modelAndView;
	}
	
	
	@RequestMapping("/logout")
	public String casLogout(HttpServletRequest request) {
		HttpSession session = request.getSession();
		session.invalidate();
		logger.info("application cas log out");
		return "redirect:" + casLogoutUrl;
	}

	public String getCurrentUrl(HttpServletRequest request) {
		String uri = request.getScheme() + "://" + request.getServerName()
				+ ("https".equals(request.getScheme()) && request.getServerPort() == 80
						|| "https".equals(request.getScheme()) && request.getServerPort() == 443 ? ""
								: ":" + request.getServerPort())
				+ request.getRequestURI();
		logger.info("getCurrentUrl="+ uri);
		return uri;
	}
	
}
