package com.capgroup.dcip.sand.company;

import java.util.Optional;

/**
 * Repository for retrieving company summary info from the SAndP database
 */
public interface CompanySummaryRepository {
	
	Optional<CompanySummary> findSummaryById(long id);
	
}
