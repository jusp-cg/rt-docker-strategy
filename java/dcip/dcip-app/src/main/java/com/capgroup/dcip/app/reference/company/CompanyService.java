package com.capgroup.dcip.app.reference.company;

import com.capgroup.dcip.app.ResourceNotFoundException;
import com.capgroup.dcip.domain.reference.company.SymbolType;

import java.util.EnumSet;
import java.util.Optional;

/**
 * Contract for retrieving company information
 */
public interface CompanyService {

    static CompanyModel findByIdOrUnknown(CompanyService companyService, long id) {
        return companyService.findById(id).orElseGet(() -> CompanyModel.CreateUnknown(id));
    }

    Iterable<CompanyModel> findAll(String matches, EnumSet<CompanyType> companyTypes);

    Iterable<CompanyModel> findAll(String tickerSymbol, String exchangeSymbol, String symbol, SymbolType symbolType);

    Optional<CompanyModel> findById(long id);

    default CompanyModel findByIdOrElseThrow(long id) {
        return findById(id).orElseThrow(() -> new ResourceNotFoundException("Company", Long.toString(id)));
    }
}
