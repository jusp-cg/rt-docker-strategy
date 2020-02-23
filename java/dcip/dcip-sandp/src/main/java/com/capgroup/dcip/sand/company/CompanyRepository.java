package com.capgroup.dcip.sand.company;

import com.capgroup.dcip.domain.reference.company.SymbolType;

import java.util.Optional;

/**
 * Repository for retrieving company info from the SAndP database
 */
public interface CompanyRepository {
    Iterable<Company> findAll(String match, boolean publicCompany, boolean privateCompany,
							  boolean publicPrivateCompany);

    Iterable<Company> findAll(String tickerSymbol, String exchangeSymbol,
                              String symbol, SymbolType symbolType);


    Optional<Company> findById(long id);
}
