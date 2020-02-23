package com.capgroup.dcip.app.reference.capital_system;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.capgroup.dcip.app.MappersTestConfig;
import com.capgroup.dcip.app.reference.capital_system.CmpsAccountMapperImpl;
import com.capgroup.dcip.cmps.model.Account;

@ContextConfiguration(classes = MappersTestConfig.class, loader = AnnotationConfigContextLoader.class)
@RunWith(SpringRunner.class)
@Import(CmpsAccountMapperImpl.class)
public class CmpsAccountMapperTest {

	@Autowired
	CmpsAccountMapper accountMapper;
	
	@Test
	public void simpleTest() {
		Account account = new Account();
		account.setAccountUid(12);
		account.setPortfolioUid(13l);
		account.setAccountNumber("1234");
		account.setLegalName("legalName");
		
		AccountModel accountModel = accountMapper.toAccountModel(account);
		assertEquals(13l, accountModel.getId());
		assertEquals("legalName", accountModel.getName());
	}
}
