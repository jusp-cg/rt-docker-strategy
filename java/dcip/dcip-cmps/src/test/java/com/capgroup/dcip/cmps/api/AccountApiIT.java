package com.capgroup.dcip.cmps.api;

import java.util.ArrayList;
import java.util.Collections;

import org.assertj.core.internal.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.capgroup.dcip.cmps.CmpsTestConfig;
import com.capgroup.dcip.cmps.model.AccountsResponse;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CmpsTestConfig.class)//, loader = AnnotationConfigContextLoader.class)
public class AccountApiIT {
	@Autowired
	AccountApi accountApi;
	
	//@Test
	public void test()
	{
		AccountsResponse result = accountApi.accountGetAllAccounts(null, null, null, null, null, null, null);
	}
	
	@Test
	public void testOneAccount() {
		AccountsResponse result = accountApi.accountGetAllAccounts(Collections.singletonList("11223366"), null, null, null, null, null, null);
	}
}
