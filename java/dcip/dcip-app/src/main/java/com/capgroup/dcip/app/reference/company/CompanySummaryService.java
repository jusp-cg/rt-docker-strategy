package com.capgroup.dcip.app.reference.company;

import org.springframework.aop.framework.AopContext;

import java.util.EnumSet;
import java.util.Optional;

/**
 * Contract for retrieving company information
 */
public interface CompanySummaryService {

	Optional<CompanySummaryModel> findSummaryById(long id);
	
	static CompanySummaryModel findByIdOrUnknown(CompanySummaryService companySummaryService, long id) {
		return companySummaryService.findSummaryById(id).orElseGet(()->CompanySummaryModel.CreateUnknown(id));	
    }

	
	
}
