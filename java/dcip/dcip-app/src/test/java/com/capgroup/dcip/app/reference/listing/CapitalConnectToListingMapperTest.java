package com.capgroup.dcip.app.reference.listing;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.capgroup.dcip.app.MappersTestConfig;
import com.capgroup.dcip.capitalconnect.company.Company;

@ContextConfiguration(classes = MappersTestConfig.class, loader = AnnotationConfigContextLoader.class)
@RunWith(SpringRunner.class)
@Import(CapitalConnectToListingMapperImpl.class)
public class CapitalConnectToListingMapperTest {

	@Autowired
	private CapitalConnectToListingMapper mapper;

	@Test
	public void toModelTest() {
		Company input = new Company();
		input.setId("id");
		input.setInstrumentCusipIdentifier("cusipId");
		input.setInstrumentIsinIdentifier("isin");
		input.setIssuerDescription("description");
		input.setIssuerLongName("longName");
		input.setIssuerUniqueIdentifier("uniqueId");
		input.setListingBloombergCompositeTickerSymbol("BBTicker");
		input.setListingCgSymbol("cgSymbol");
		input.setListingCurrencyUniqueIdentifier(10);
		input.setListingSedolIdentifier("sedol");
		input.setListingTradingReutersInstrumentCode("ric");
		input.setListingType("type");
		input.setListingUniqueIdentifier("uniqueIdentifier");
		input.setName("name");
		input.setPreferredName("preferredName");
		input.setPrimaryTickerSymbol("primaryTickerSymbol");
		input.setSource("source");

		ListingModel result = mapper.toListingModel(input);

		assertEquals("cusipId", result.getIdentifiers().getCusip());
		assertEquals("id", result.getId());
		assertEquals("isin", result.getIdentifiers().getIsin());
		assertEquals("description", result.getIssuerDescription());
		
		assertEquals("longName", result.getIssuerLongName());
		assertEquals("uniqueId", result.getIssuerUniqueIdentifier());
		assertEquals("BBTicker", result.getListingBloombergCompositeTickerSymbol());
		assertEquals("cgSymbol", result.getIdentifiers().getCgSymbol());
		assertEquals(10, result.getListingCurrencyUniqueIdentifier());
		assertEquals("sedol", result.getIdentifiers().getSedol());
		assertEquals("ric", result.getIdentifiers().getRic());
		assertEquals("type", result.getListingType());
		assertEquals("uniqueIdentifier", result.getListingUniqueIdentifier());
		assertEquals("name", result.getName());
		assertEquals("preferredName", result.getPreferredName());
		assertEquals("primaryTickerSymbol", result.getPrimaryTickerSymbol());
		assertEquals("source", result.getSource());

	}
}
