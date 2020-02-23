package com.capgroup.dcip.app.journal;

/**
 * Application level service for retrieving/manipulating InvestmentActions (i.e. journal entries)
 */
public interface InvestmentActionService {

    Iterable<InvestmentActionModel> findAll(Long profileId);

    InvestmentActionModel findById(long id);

    Iterable<InvestmentActionModel> findByCompanyIdAndProfileId(long companyId, Long profileId);

    InvestmentActionModel create(InvestmentActionModel investmentActionModel);

    InvestmentActionModel update(long id, InvestmentActionModel investmentActionModel);

    void delete(long id);
}
