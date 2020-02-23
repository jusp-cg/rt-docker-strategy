package com.capgroup.dcip.app.reference.company;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class CompanySummaryModel {
		
	
	private long companyId;
	private String companyName;
	private String countryLoc;
	private String tradingCurrency;
	private String companyTicker;
	private String marketCap;
	private String exchangeRate;
	private String exchange;
	private String percentFloat;
	private String latestPrice;
	private String dividendPerShare;
	private String reportingCurrency;
	private String reportingExchangeRate;
	private String dailyVolume;
	private String companyDescription;
	private String fiscalYearEnd;	
	
	//Company Report Variables
	private List<String> reportItem;
	Map<String, List<String>> historicData;
	
	
	public static CompanySummaryModel CreateUnknown(long companyId) {
		CompanySummaryModel result = new CompanySummaryModel();
		result.companyId = companyId;
		result.companyName = "#UNKNOWN#";
		return result;
	}	
	
}
