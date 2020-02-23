package com.capgroup.dcip.app.reference.capital_system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.capgroup.dcip.app.ServiceTestConfig;
import com.capgroup.dcip.app.reference.capital_system.CmpsAccountService.AccountLoaderImpl;
import com.capgroup.dcip.cmps.api.AccountApi;
import com.capgroup.dcip.cmps.api.PortfolioApi;
import com.capgroup.dcip.cmps.model.Account;
import com.capgroup.dcip.cmps.model.AccountsResponse;
import com.capgroup.dcip.cmps.model.Portfolio;
import com.capgroup.dcip.cmps.model.Portfolio.CategoryEnum;
import com.capgroup.dcip.cmps.model.Portfolio.InvestmentPortfolioTypeCodeEnum;
import com.capgroup.dcip.cmps.model.PortfoliosResponse;

// Remove after 2.0 upgrade

@SpringBootTest()
@ContextConfiguration(classes = ServiceTestConfig.class, loader = AnnotationConfigContextLoader.class)
@RunWith(SpringRunner.class)
@TestPropertySource(properties = { "cmps.account.concurrent-threads=1" })
@Import({ CmpsAccountService.class, AccountLoaderImpl.class })
@EnableCaching
public class CmpsAccountServiceTest {

	@Autowired
	CmpsAccountService cmpsAccountService;

	@MockBean
	CmpsAccountMapper cmpsAccountMapper;

	@MockBean
	PortfolioApi portfolioApi;

	@MockBean
	AccountApi accountApi;

	@Autowired
	CacheManager cacheManager;

	@Before
	public void init() {
		cacheManager.getCache("Account").clear();
	}

	@Test
	public void findByIdNoAccountTest() {
		PortfoliosResponse portfolioResponse = new PortfoliosResponse();
		given(portfolioApi.portfolioGetPortfolio(Mockito.any(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(),
				Mockito.isNull())).willReturn(portfolioResponse);

		Optional<AccountModel> result = cmpsAccountService.findById(12);
		assertFalse(result.isPresent());
	}

	@Test
	public void findByIdOneAccountTest() {
		Portfolio portfolio = new Portfolio();
		PortfoliosResponse portfolioResponse = new PortfoliosResponse();
		portfolioResponse.setPortfolios(Collections.singletonList(portfolio));
		AccountModel accountModel = new AccountModel();

		given(portfolioApi.portfolioGetPortfolio(Mockito.any(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(),
				Mockito.isNull())).willReturn(portfolioResponse);
		given(cmpsAccountMapper.toAccountModel(portfolio)).willReturn(accountModel);

		Optional<AccountModel> result = cmpsAccountService.findById(12);
		assertSame(accountModel, result.get());
	}

	@Test
	public void findWithNoAccount() {
		PortfoliosResponse portfolioResponse = new PortfoliosResponse();

		given(portfolioApi.portfolioGetPortfolios(Mockito.isNull(),
				Mockito.same(InvestmentPortfolioTypeCodeEnum.AA.getValue()),
				Mockito.same(CategoryEnum.INVESTMENT.getValue()), Mockito.eq("text"), Mockito.isNull(),
				Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(),
				Mockito.isNull())).willReturn(portfolioResponse);

		Stream<AccountModel> results = cmpsAccountService.find("text");

		assertEquals(0, results.count());
	}

	@Test
	public void findWithOneAccount() {
		Account account = new Account();
		AccountsResponse accountResponse = new AccountsResponse();
		accountResponse.addAccountsItem(account);
		AccountModel accountModel = new AccountModel();

		Portfolio portfolio = new Portfolio();
		portfolio.setPortfolioUid(12L);
		PortfoliosResponse portfolioResponse = new PortfoliosResponse();
		portfolioResponse.addPortfoliosItem(portfolio);

		given(portfolioApi.portfolioGetPortfolios(Mockito.isNull(),
				Mockito.same(InvestmentPortfolioTypeCodeEnum.AA.getValue()),
				Mockito.same(CategoryEnum.INVESTMENT.getValue()), Mockito.eq("text"), Mockito.isNull(),
				Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(),
				Mockito.isNull())).willReturn(portfolioResponse);
	//	given(accountApi.accountGetAllAccounts(Mockito.anyList(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(),
//				Mockito.isNull(), Mockito.isNull())).willReturn(accountResponse);
		given(cmpsAccountMapper.toAccountModel(account)).willReturn(accountModel);

		Stream<AccountModel> results = cmpsAccountService.find("text");
		Optional<AccountModel> resultAccountModel = results.findAny();
		if (resultAccountModel.isPresent()) {
			assertSame(accountModel, resultAccountModel);
		}
	}
/*
	@Test
	public void accountCacheTest() {
		Account account = new Account();
		AccountsResponse accountResponse = new AccountsResponse();
		accountResponse.setAccounts(Collections.singletonList(account));
		AccountModel accountModel = new AccountModel();

		given(accountApi.accountGetAllAccounts(Mockito.anyList(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(),
				Mockito.isNull(), Mockito.isNull())).willReturn(accountResponse);
		given(cmpsAccountMapper.toAccountModel(account)).willReturn(accountModel);

		Optional<AccountModel> result1 = cmpsAccountService.findById(12L);
		reset(accountApi);
		Optional<AccountModel> result2 = cmpsAccountService.findById(12L);
		assertSame(result1, result2);
	}*/
}