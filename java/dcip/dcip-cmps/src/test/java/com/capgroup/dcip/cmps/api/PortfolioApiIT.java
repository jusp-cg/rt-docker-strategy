package com.capgroup.dcip.cmps.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.capgroup.dcip.cmps.CmpsTestConfig;
import com.capgroup.dcip.cmps.model.PortfoliosResponse;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CmpsTestConfig.class)
public class PortfolioApiIT {
	@Autowired
	PortfolioApi portfolioApi;
	
	@Test
	public void test() {
		PortfoliosResponse result = portfolioApi.portfolioGetPortfolios(null, null, null, "avx", null, null, null, null, null, null, null);
	}
}
