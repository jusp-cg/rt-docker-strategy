package com.capgroup.dcip.app.reference.listing;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;

import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.capgroup.dcip.app.ServiceTestConfig;
import com.capgroup.dcip.capitalconnect.company.CapitalConnectCompanyService;
import com.capgroup.dcip.capitalconnect.suggestion.CapitalConnectSuggestionService;
import com.capgroup.dcip.capitalconnect.suggestion.SearchResult;

@SpringBootTest()
@ContextConfiguration(classes = ServiceTestConfig.class, loader = AnnotationConfigContextLoader.class)
@RunWith(SpringRunner.class)
@Import(CapitalConnectListingService.class)
@TestPropertySource(properties = { "capital-connect.listing.concurrent-threads=3" })
public class CapitalConnectListingServiceTest {

	@Autowired
	CapitalConnectListingService listingService;

	@MockBean
	CapitalConnectSuggestionService capitalConnectSuggestionService;

	@MockBean
	CapitalConnectToListingMapper mapper;

	@MockBean
	CapitalConnectCompanyService capitalConnectCompanyService;

	@Test
	public void findNoCompaniesTest() {
		SearchResult searchResult = new SearchResult();

		given(capitalConnectSuggestionService.search(Mockito.any())).willReturn(searchResult);
		given(mapper.toListingModels(searchResult)).willReturn(Stream.empty());

		Stream<ListingModel> model = listingService.find("criteria");

		assertEquals(0, model.count());
	}

	@Configuration
	static class Config {

		@Bean
		public static PropertySourcesPlaceholderConfigurer propertiesResolver() {
			return new PropertySourcesPlaceholderConfigurer();
		}

	}
}
