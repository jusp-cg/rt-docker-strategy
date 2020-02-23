package com.capgroup.dcip.capitalconnect.company;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Company {
	private String id;
	private String listingType;
	private String listingUniqueIdentifier;
	private String listingCgSymbol;
	private String listingBloombergCompositeTickerSymbol;
	private String listingSedolIdentifier;
	private String listingTradingReutersInstrumentCode;
	private int listingCurrencyUniqueIdentifier;
	private String instrumentCusipIdentifier;
	private String instrumentIsinIdentifier;
	private String name;
	private String issuerUniqueIdentifier;
	private String primaryTickerSymbol;
	private String issuerLongName;
	private String issuerDescription;
	private String preferredName;
	private String source;
}
