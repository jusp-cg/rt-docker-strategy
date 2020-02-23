package com.capgroup.dcip.capitalconnect.suggestion;

import com.capgroup.dcip.capitalconnect.company.Company;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchResult {
	private SearchResultItems<Company> companies;
}
