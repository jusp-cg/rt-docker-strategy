package com.capgroup.dcip.app.identity;

/**
 * Service for finding Investment Units 
 */
public interface InvestmentUnitService {
	Iterable<InvestmentUnitModel> findAll();
	
	InvestmentUnitModel findById(long id);
}
