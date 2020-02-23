package com.capgroup.dcip.sand.company;

import com.capgroup.dcip.domain.reference.company.SymbolType;

import java.util.Optional;

/**
 * API for accessing company details 
 */
public interface SAndPCompanyGateway {
	Iterable<Company> findAll(String tickerSymbol, String exchangeSymbol, String symbol, SymbolType symbolType);

	Iterable<Company> findAll(String matches, boolean publicCompany, boolean privateCompany, boolean publicPrivateCompany);
	
	Optional<Company> findById(long id);

	Optional<CompanySummary> findSummaryById(long id);
}
