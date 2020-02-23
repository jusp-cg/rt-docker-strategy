package com.capgroup.dcip.capitalconnect.suggestion;

import java.util.Arrays;
import java.util.List;

import lombok.Data;

@Data
public class MultiSuggestionContext {
	private String term;
	private List<Scope> scopes;
	
	public MultiSuggestionContext(String term, Scope... scopes) {
		this.term = term;
		this.scopes = Arrays.asList(scopes);
	}
}
