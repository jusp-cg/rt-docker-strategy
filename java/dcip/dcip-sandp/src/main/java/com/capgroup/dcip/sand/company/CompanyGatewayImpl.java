package com.capgroup.dcip.sand.company;

import com.capgroup.dcip.domain.reference.company.SymbolType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CompanyGatewayImpl implements SAndPCompanyGateway {

    private CompanyRepository companyRepository;
    private CompanySummaryRepository companySummaryRepository;

    @Autowired
    public CompanyGatewayImpl(CompanyRepository companyRepository, CompanySummaryRepository companySummaryRepository) {
        this.companyRepository = companyRepository;
        this.companySummaryRepository = companySummaryRepository;

    }

    @Override
    public Iterable<Company> findAll(String match, boolean publicCompany, boolean privateCompany,
                                     boolean publicPrivateCompany) {
        return companyRepository.findAll(match, publicCompany, privateCompany, publicPrivateCompany);
    }

    @Override
    public Iterable<Company> findAll(String tickerSymbol, String exchangeSymbol,
                                     String symbol, SymbolType symbolType) {
        return companyRepository.findAll(tickerSymbol, exchangeSymbol,
                symbol, symbolType);
    }


    @Override
    public Optional<Company> findById(long id) {
        return companyRepository.findById(id);
    }

    @Override
    public Optional<CompanySummary> findSummaryById(long id) {
        return companySummaryRepository.findSummaryById(id);
    }

}
