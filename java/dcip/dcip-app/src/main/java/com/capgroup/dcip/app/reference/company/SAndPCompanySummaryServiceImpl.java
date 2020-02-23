package com.capgroup.dcip.app.reference.company;

import com.capgroup.dcip.app.identity.IdentityMappingService;
import com.capgroup.dcip.sand.company.SAndPCompanyGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class SAndPCompanySummaryServiceImpl implements CompanySummaryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SAndPCompanyServiceImpl.class);
    private SAndPCompanyGateway companyGateway;
    private SAndPCompanyMapper sAndPCompanyMapper;
    private IdentityMappingService identityMappingService;

    @Autowired
    public SAndPCompanySummaryServiceImpl(SAndPCompanyGateway companyGateway, SAndPCompanyMapper sAndPCompanyMapper,
                                          IdentityMappingService identityMappingService) {
        this.companyGateway = companyGateway;
        this.sAndPCompanyMapper = sAndPCompanyMapper;
        this.identityMappingService = identityMappingService;
    }


    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
    public Optional<CompanySummaryModel> findSummaryById(long id) {
        // findAll the S&P id
        Optional<String> eId = identityMappingService.externalIdentifier(id, 1, 14);
        return eId.flatMap(externalId -> companyGateway.findSummaryById(Long.parseLong(externalId))).map(sAndPCompanyMapper::map);

    }
}
