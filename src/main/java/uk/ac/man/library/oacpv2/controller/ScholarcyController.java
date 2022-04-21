package uk.ac.man.library.oacpv2.controller;

import java.io.Console;
import java.net.Proxy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.man.library.oacpv2.model.User;
import uk.ac.man.library.oacpv2.objects.scholarcy.FileUrl;
import uk.ac.man.library.oacpv2.objects.scholarcy.KnowledgeResponse;

@Controller
public class ScholarcyController {

	private static final Logger logger = LoggerFactory.getLogger(ScholarcyController.class.getName());

	@Value("${scholarly.apikey}")
	String authToken;

	@Value("${scholarURL}")
	String scholarURL;

	@GetMapping("/scholarcydata")
	public String getUrl(@RequestParam("name") String name, Model model) {
		model.addAttribute("fileUrl", new FileUrl());
		return "scholarcy/response_page";
	}

	@PostMapping("/scholarcydata")
	public ModelAndView getDatafromScholarandRender(@RequestParam("name") String fileUrl, 
					HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		ModelAndView modelAndView = new ModelAndView();
		if (session != null) {
			if (session.getAttribute("isValid") == "yes") {
				User user = (User) session.getAttribute("user");

				logger.info("scholarcy/response_page, logged username: " + user.getFullName());
				modelAndView.addObject("user", user);
				
				final String urlString = scholarURL + "?url=" + fileUrl;
				RestTemplate restTemplate = new RestTemplate();
				HttpHeaders headers = new HttpHeaders();
				headers.set("Authorization", "Bearer " + authToken);
				KnowledgeResponse knowledgeRepsonse = new KnowledgeResponse();
				try {
					knowledgeRepsonse = restTemplate.getForObject(urlString, KnowledgeResponse.class);
				} catch (HttpClientErrorException e) {
					logger.info("When calling the scholarcy api, catch HttpClientErrorException: ");
				}
				modelAndView.addObject("knowledgeRepsonse", knowledgeRepsonse);
				modelAndView.setViewName("scholarcy/response_page");
			} else {
				logger.info("No Valid");
				modelAndView.setViewName("redirect:/");
			}
			
		}else {
			logger.info("No session");
			modelAndView.setViewName("redirect:/");
		}
		
	return modelAndView;
		
	}

}
