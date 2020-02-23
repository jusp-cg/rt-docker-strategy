package com.capgroup.dcip.capitalconnect.company;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CapitalConnectCompanyServiceImpl implements CapitalConnectCompanyService {
	private RestTemplate restTemplate;

	@Autowired
	public CapitalConnectCompanyServiceImpl(@Qualifier("capitalConnectRestTemplate") RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@Override
	public Company find(String id) {
		return restTemplate.getForObject("/companies.svc/companies/" + id, Company.class);
	}
}
