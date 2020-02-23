package com.capgroup.dcip.webapi.controllers;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.Stream;

import com.capgroup.dcip.webapi.controllers.reference.AccountController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.web.servlet.MockMvc;

import com.capgroup.dcip.app.reference.capital_system.AccountModel;
import com.capgroup.dcip.app.reference.capital_system.AccountService;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ControllersTestConfig.class, loader = AnnotationConfigWebContextLoader.class)
@WebMvcTest(controllers = AccountController.class, secure = false)
@Import(AccountController.class)
public class AccountControllerTest {

	@MockBean
	AccountService accountService;

	@Autowired
	MockMvc mockMvc;

	@Test
	public void findAllWithNoMatches() throws Exception {
		given(accountService.find("noMatch")).willReturn(Stream.empty());

		mockMvc.perform(get("/api/dcip/reference/accounts?matches=noMatch").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(0));
	}

	@Test
	public void getWithAMatch() throws Exception {
		AccountModel account = new AccountModel();
		account.setId(1235);
		account.setName("name");
		given(accountService.find("aMatch")).willReturn(Stream.of(account));

		mockMvc.perform(get("/api/dcip/reference/accounts?matches=aMatch").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value("1235"))
				.andExpect(jsonPath("$[0].name").value("name"));
	}
}
