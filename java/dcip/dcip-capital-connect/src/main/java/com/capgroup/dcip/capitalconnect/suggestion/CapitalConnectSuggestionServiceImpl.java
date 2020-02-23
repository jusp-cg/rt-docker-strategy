package com.capgroup.dcip.capitalconnect.suggestion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CapitalConnectSuggestionServiceImpl implements CapitalConnectSuggestionService {

	private RestTemplate restTemplate;

	@Autowired
	public CapitalConnectSuggestionServiceImpl(@Qualifier("capitalConnectRestTemplate") RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@Override
	public SearchResult search(MultiSuggestionContext context) {
		return restTemplate.postForObject("/suggestions.svc/multi", context, SearchResult.class);
	}
}
