package com.capgroup.dcip.capitalconnect;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.capgroup.dcip.capitalconnect.suggestion.CapitalConnectSuggestionService;
import com.capgroup.dcip.capitalconnect.suggestion.MultiSuggestionContext;
import com.capgroup.dcip.capitalconnect.suggestion.Scope;
import com.capgroup.dcip.capitalconnect.suggestion.SearchResult;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CapitalConnectTestConfig.class, loader = AnnotationConfigContextLoader.class)
public class CapitalConnectSuggestionServiceImplIT {
	@Autowired
	CapitalConnectSuggestionService capitalConnectSuggestionService;

	@Test
	public void noResultsTest() {
		MultiSuggestionContext context = new MultiSuggestionContext("xxxxxx", new Scope("company"));
		SearchResult result = this.capitalConnectSuggestionService.search(context);

		assertNotNull(result);
	}
}
