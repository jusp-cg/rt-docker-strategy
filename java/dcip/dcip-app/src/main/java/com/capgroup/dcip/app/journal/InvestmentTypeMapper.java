package com.capgroup.dcip.app.journal;

import com.capgroup.dcip.app.entity.TemporalEntityMapper;
import com.capgroup.dcip.domain.journal.InvestmentType;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

/**
 * Responsible for converting between an InvestmentType and an
 * InvestmentTypeModel (and vice-versa)
 */
@Mapper(config = TemporalEntityMapper.class)
public interface InvestmentTypeMapper {
    InvestmentTypeModel map(InvestmentType investmentType);

    InvestmentType map(InvestmentTypeModel model);

    Iterable<InvestmentTypeModel> mapAll(Iterable<InvestmentType> investmentTypes);

    InvestmentType update(InvestmentTypeModel typeModel, @MappingTarget InvestmentType investmentType);
}
