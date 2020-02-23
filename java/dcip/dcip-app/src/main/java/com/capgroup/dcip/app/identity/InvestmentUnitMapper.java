package com.capgroup.dcip.app.identity;

import org.mapstruct.Mapper;

import com.capgroup.dcip.domain.identity.InvestmentUnit;

/**
 * Mapping between InvestmentUnit and InvestmentUnitModel
 */
@Mapper
public interface InvestmentUnitMapper {
	InvestmentUnitModel map(InvestmentUnit unit);
	
	Iterable<InvestmentUnitModel> map(Iterable<InvestmentUnit> investmentUnits);
}
