package com.capgroup.dcip.app.journal;

import java.util.stream.Stream;

public interface InvestmentActionTypeService {
    Stream<InvestmentActionTypeModel> findByProfileId(Long profileId);

    Stream<InvestmentActionTypeModel> findAll();

    InvestmentActionTypeModel findById(long id);

    InvestmentActionTypeModel create(InvestmentActionTypeModel investmentActionTypeModel);

    InvestmentActionTypeModel update(long id, InvestmentActionTypeModel investmentActionTypeModel);

    void delete(long id);
}
