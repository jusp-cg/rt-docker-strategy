package com.capgroup.dcip.app.journal;

import com.capgroup.dcip.app.entity.TemporalEntityMapper;
import com.capgroup.dcip.domain.journal.InvestmentActionType;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.stream.Stream;

/**
 * Responsible for converting between an InvestmentActionType and an
 * InvestmentActionTypeModel (and vice-versa)
 */
@Mapper(config = TemporalEntityMapper.class)
public abstract class InvestmentActionTypeMapper {
    public abstract InvestmentActionTypeModel map(InvestmentActionType investmentAction);

    public abstract InvestmentActionType map(InvestmentActionTypeModel investmentActionTypeModel);

    public abstract Stream<InvestmentActionTypeModel> mapAll(Stream<InvestmentActionType> InvestmentActionTypes);

    public abstract void updateInvestmentActionType(InvestmentActionTypeModel investmentActionModel, @MappingTarget InvestmentActionType investmentAction);

    @AfterMapping
    protected void afterMapping(InvestmentActionType investmentActionType, @MappingTarget InvestmentActionTypeModel model) {
        if (investmentActionType.getGroup() != null) model.setGroupName(investmentActionType.getGroup().getName());
    }
}
