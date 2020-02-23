package com.capgroup.dcip.app.reference.listing;

import lombok.Data;

@Data
public class ListingIdentifiersModel {
	private String ric;
	private String isin;
	private String cusip;
	private String sedol;
	private String cgSymbol;
}
