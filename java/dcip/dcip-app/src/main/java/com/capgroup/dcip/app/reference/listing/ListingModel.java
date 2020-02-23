package com.capgroup.dcip.app.reference.listing;

import lombok.Data;

@Data
public class ListingModel {
	private ListingIdentifiersModel identifiers;
	private String id;
	private String listingType;
	private String listingUniqueIdentifier;
	private String listingBloombergCompositeTickerSymbol;
	private int listingCurrencyUniqueIdentifier;
	private String name;
	private String issuerUniqueIdentifier;
	private String primaryTickerSymbol;
	private String issuerLongName;
	private String issuerDescription;
	private String preferredName;
	private String source;
	private boolean isError;
	
	public static ListingModel CreateError(String id) {
		ListingModel result = new ListingModel();
		result.id = id;
		result.isError = true;
		result.name = "#ERROR#";
		result.listingBloombergCompositeTickerSymbol = "#ERROR#";
		result.preferredName = "#ERROR#";
		return result;
	}
	
	public static ListingModel CreateUnknown(String id) {
		ListingModel result = new ListingModel();
		result.id = id;
		result.isError = true;
		result.name = "#UNKNOWN#";
		result.listingBloombergCompositeTickerSymbol = "#UNKNOWN#";
		result.preferredName = "#UNKNOWN#";
		return result;
	}

}
