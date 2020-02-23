package com.capgroup.dcip.app.journal;

public interface InvestmentReasonService {
    Iterable<InvestmentReasonModel> findByUserInitials(String userInitials, Boolean includeDefaults);

    InvestmentReasonModel create(InvestmentReasonModel investmentTypeModel);

    InvestmentReasonModel update(long id, InvestmentReasonModel typeModel);

    InvestmentReasonModel delete(long id);
}
