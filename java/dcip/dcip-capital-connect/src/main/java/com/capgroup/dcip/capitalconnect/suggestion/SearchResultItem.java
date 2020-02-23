package com.capgroup.dcip.capitalconnect.suggestion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchResultItem<T> {
	private Integer hitCount;
	private T item;
}
