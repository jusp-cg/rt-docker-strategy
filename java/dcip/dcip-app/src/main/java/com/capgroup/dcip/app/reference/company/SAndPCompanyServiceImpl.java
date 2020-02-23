package com.capgroup.dcip.app.reference.company;

import com.capgroup.dcip.app.identity.IdentityMappingService;
import com.capgroup.dcip.domain.reference.company.SymbolType;
import com.capgroup.dcip.sand.company.Company;
import com.capgroup.dcip.sand.company.SAndPCompanyGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class SAndPCompanyServiceImpl implements CompanyService {

    private SAndPCompanyGateway companyGateway;
    private SAndPCompanyMapper sAndPCompanyMapper;
    private IdentityMappingService identityMappingService;

    @Autowired
    public SAndPCompanyServiceImpl(SAndPCompanyGateway companyGateway, SAndPCompanyMapper sAndPCompanyMapper,
                                   IdentityMappingService identityMappingService) {
        this.companyGateway = companyGateway;
        this.sAndPCompanyMapper = sAndPCompanyMapper;
        this.identityMappingService = identityMappingService;
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
    @Override
    public Iterable<CompanyModel> findAll(String tickerSymbol, String exchangeSymbol,
                                          String symbol, SymbolType symbolType) {
        // call the gateway to get the company info
        Iterable<Company> sAndPCompanies = StreamSupport
                .stream(companyGateway.findAll(tickerSymbol, exchangeSymbol,
                        symbol, symbolType).spliterator(), false)
                .collect(Collectors.toList());

        return postRead(sAndPCompanies);
    }


    private Iterable<CompanyModel> postRead(Iterable<Company> sAndPCompanies){
        // prime the cache with ids
        identityMappingService.internalIdentifiers(SAndPConstants.SANDP_DATASOURCE_ID, SAndPConstants.COMPANY_TYPE_ID,
                StreamSupport.stream(sAndPCompanies.spliterator(), false).map(x -> Long.toString(x.getId()))
                        .collect(Collectors.toList()));

        identityMappingService.internalIdentifiers(SAndPConstants.SANDP_DATASOURCE_ID, SAndPConstants.SECURITY_TYPE_ID,
                StreamSupport.stream(sAndPCompanies.spliterator(), false).flatMap(x -> x.getSecurities().stream())
                        .flatMap(x -> x.getTradingItems().stream()).map(x -> Long.toString(x.getId()))
                        .collect(Collectors.toList()));

        // convert the S&P representation to the domain model
        return sAndPCompanyMapper.mapCompanies(sAndPCompanies);
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
    @Override
    public Iterable<CompanyModel> findAll(String matches, EnumSet<CompanyType> companyTypes) {
        // call the gateway to get the company info
        Iterable<Company> sAndPCompanies = StreamSupport
                .stream(companyGateway.findAll(matches, companyTypes.contains(CompanyType.PUBLIC),
                        companyTypes.contains(CompanyType.PRIVATE),
                        companyTypes.contains(CompanyType.PUBLIC_TO_PRIVATE)).spliterator(), false)
                .collect(Collectors.toList());

        return postRead(sAndPCompanies);
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
    @Cacheable("Company")
    public Optional<CompanyModel> findById(long id) {
        // findAll the S&P id
        Optional<String> eId = identityMappingService.externalIdentifier(id, 1, 14);
        return eId.flatMap(externalId -> companyGateway.findById(Long.parseLong(externalId))).map(sAndPCompanyMapper::map);
    }
}
