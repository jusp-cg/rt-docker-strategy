package com.capgroup.dcip.capitalconnect.suggestion;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchResultItems<T> {
	private boolean lastResult;
	private int nextStartRow;
	private int totalRows;
	
	private List<SearchResultItem<T>> items;
}
