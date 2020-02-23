package com.capgroup.dcip.app.journal;

import com.capgroup.dcip.app.entity.TemporalEntityMapper;
import com.capgroup.dcip.domain.journal.InvestmentReason;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = TemporalEntityMapper.class)
public interface InvestmentReasonMapper {
    InvestmentReasonModel map(InvestmentReason investmentReason);

    InvestmentReason map(InvestmentReasonModel model);

    Iterable<InvestmentReasonModel> map(Iterable<InvestmentReason> reasons);

    void update(InvestmentReasonModel reasonModel, @MappingTarget InvestmentReason reason);
}
