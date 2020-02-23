package com.capgroup.dcip.app.journal;

/**
 * Service for creating/manipulating/etc. InvestmentTypes
 */
public interface InvestmentTypeService {
    Iterable<InvestmentTypeModel> findByUserInitials(String userInitials, Boolean includeDefaults);

    InvestmentTypeModel create(InvestmentTypeModel investmentTypeModel);

    InvestmentTypeModel update(long id, InvestmentTypeModel typeModel);

    InvestmentTypeModel delete(long id);
}
