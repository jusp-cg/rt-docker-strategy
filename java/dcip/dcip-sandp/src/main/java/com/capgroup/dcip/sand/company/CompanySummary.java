package com.capgroup.dcip.sand.company;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CompanySummary implements Comparable<CompanySummary> {
	
	//Company Summary Header Variables
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
		private Map<String, List<String>> historicData;
		
	@Override
	public int compareTo(CompanySummary o) {
		int result = Long.compare(companyId, o.companyId);
		return result == 0 ? companyName.compareTo(o.companyName) : result;
	}
	
	public CompanySummary(){
		//Default Constructor
	}

	public long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCountryLoc() {
		return countryLoc;
	}

	public void setCountryLoc(String countryLoc) {
		this.countryLoc = countryLoc;
	}
	
	public String getTradingCurrency() {
		return tradingCurrency;
	}

	public void setTradingCurrency(String tradingCurrency) {
		this.tradingCurrency = tradingCurrency;
	}
	
	public String getCompanyTicker() {
		return companyTicker;
	}

	public void setCompanyTicker(String companyTicker) {
		this.companyTicker = companyTicker;
	}
	
	
	public String getMarketCap() {
		return marketCap;
	}

	public void setMarketCap(String marketCap) {
		this.marketCap = marketCap;
	}

	public String getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(String exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public String getExchange() {
		return exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	public String getPercentFloat() {
		return percentFloat;
	}

	public void setPercentFloat(String percentFloat) {
		this.percentFloat = percentFloat;
	}

	public String getLatestPrice() {
		return latestPrice;
	}

	public void setLatestPrice(String latestPrice) {
		this.latestPrice = latestPrice;
	}

	public String getDividendPerShare() {
		return dividendPerShare;
	}

	public void setDividendPerShare(String dividendPerShare) {
		this.dividendPerShare = dividendPerShare;
	}
	
	public String getReportingCurrency() {
		return reportingCurrency;
	}

	public void setReportingCurrency(String reportingCurrency) {
		this.reportingCurrency = reportingCurrency;
	}
	
	public String getReportingExchangeRate() {
		return reportingExchangeRate;
	}

	public void setReportingExchangeRate(String reportingExchangeRate) {
		this.reportingExchangeRate = reportingExchangeRate;
	}
	
	public String getDailyVolume() {
		return dailyVolume;
	}

	public void setDailyVolume(String dailyVolume) {
		this.dailyVolume = dailyVolume;
	}

	public String getCompanyDescription() {
		return companyDescription;
	}

	public void setCompanyDescription(String companyDescription) {
		this.companyDescription = companyDescription;
	}
	
	public String getFiscalYearEnd() {
		return fiscalYearEnd;
	}

	public void setFiscalYearEnd(String fiscalYearEnd) {
		this.fiscalYearEnd = fiscalYearEnd;
	}

	public List<String> getReportItem() {
		return reportItem;
	}

	public void setReportItem(List<String> reportItem) {
		this.reportItem = reportItem;
	}

	public Map<String, List<String>> getHistoricData() {
	return historicData;
}

public void setHistoricData(Map<String, List<String>> historicData) {
	this.historicData = historicData;
}
	
	
}

