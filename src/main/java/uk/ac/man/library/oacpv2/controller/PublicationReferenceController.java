package uk.ac.man.library.oacpv2.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import uk.ac.man.library.oacpv2.objects.scholarcy.RefResponse;

@SessionAttributes("publicationReferenceController")
@Controller
public class PublicationReferenceController {

	private static final Logger logger = LoggerFactory.getLogger(PublicationReferenceController.class.getName());

	@Value("${crossRefURL}")
	String crossRefURL;

	@PostMapping("/sync")
	public RefResponse getPublication(@RequestParam("name") String fileUrl) {

		final String urlString = crossRefURL + "?query.title=" + fileUrl;
		RestTemplate restTemplate = new RestTemplate();
		RefResponse refResponse = new RefResponse();
		try {
			try {
				refResponse = restTemplate.getForObject(urlString, RefResponse.class);
			} catch (RestClientException e) {
				e.printStackTrace();
			}
		} catch (HttpClientErrorException e) {
			logger.info("When calling the sync api, catch HttpClientErrorException: ");
		}
		return refResponse;
	}

}
