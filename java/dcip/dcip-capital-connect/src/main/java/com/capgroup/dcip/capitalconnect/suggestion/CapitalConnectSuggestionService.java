package com.capgroup.dcip.capitalconnect.suggestion;

/**
 * External interface for connection to the Capital Connect Suggestion Service
 */
public interface CapitalConnectSuggestionService {
	SearchResult search(MultiSuggestionContext context);
}
