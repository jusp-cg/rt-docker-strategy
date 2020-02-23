package com.capgroup.dcip.webapi.controllers;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.Stream;

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

import com.capgroup.dcip.app.reference.listing.ListingIdentifiersModel;
import com.capgroup.dcip.app.reference.listing.ListingModel;
import com.capgroup.dcip.app.reference.listing.ListingService;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ControllersTestConfig.class, loader = AnnotationConfigWebContextLoader.class)
@WebMvcTest(controllers = ListingController.class, secure = false)
@Import(ListingController.class)
public class ListingControllerTest {

	@MockBean
	ListingService listingService;

	@Autowired
	MockMvc mockMvc;

	@Test
	public void findAllWithNoMatches() throws Exception {
		given(listingService.find("noMatch")).willReturn(Stream.empty());

		mockMvc.perform(get("/api/dcip/listings?matches=noMatch").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void getWithAMatch() throws Exception {
		ListingIdentifiersModel identifiers = new ListingIdentifiersModel();
		identifiers.setCgSymbol("cySymbol");
		identifiers.setCusip("cusip");
		identifiers.setIsin("isin");
		identifiers.setRic("ric");
		identifiers.setSedol("sedol");

		ListingModel listing = new ListingModel();
		listing.setId("id");
		listing.setIdentifiers(identifiers);
		listing.setIssuerDescription("issuer");
		listing.setIssuerLongName("long name");
		listing.setListingBloombergCompositeTickerSymbol("bb");
		listing.setListingCurrencyUniqueIdentifier(12);
		listing.setListingType("type");
		listing.setListingUniqueIdentifier("uid");
		listing.setName("me");
		listing.setPreferredName("preferred name");
		listing.setPrimaryTickerSymbol("primary ticker");
		listing.setSource("source");

		given(listingService.find("aMatch")).willReturn(Stream.of(listing));

		mockMvc.perform(get("/api/dcip/listings?matches=aMatch").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value("id"))
				.andExpect(jsonPath("$[0].identifiers.cgSymbol").value("cySymbol"))
				.andExpect(jsonPath("$[0].identifiers.cusip").value("cusip"))
				.andExpect(jsonPath("$[0].identifiers.isin").value("isin"))
				.andExpect(jsonPath("$[0].identifiers.ric").value("ric"))
				.andExpect(jsonPath("$[0].identifiers.sedol").value("sedol"))
				.andExpect(jsonPath("$[0].issuerDescription").value("issuer"))
				.andExpect(jsonPath("$[0].issuerLongName").value("long name"))
				.andExpect(jsonPath("$[0].listingBloombergCompositeTickerSymbol").value("bb"))
				.andExpect(jsonPath("$[0].listingCurrencyUniqueIdentifier").value("12"))
				.andExpect(jsonPath("$[0].listingType").value("type"))
				.andExpect(jsonPath("$[0].listingUniqueIdentifier").value("uid"))
				.andExpect(jsonPath("$[0].name").value("me"))
				.andExpect(jsonPath("$[0].preferredName").value("preferred name"))
				.andExpect(jsonPath("$[0].primaryTickerSymbol").value("primary ticker"))
				.andExpect(jsonPath("$[0].source").value("source"));
	}
}
