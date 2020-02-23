package com.capgroup.dcip.infrastructure.repository.journal;

import com.capgroup.dcip.domain.journal.InvestmentActionType;
import com.capgroup.dcip.infrastructure.repository.TemporalEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvestmentActionTypeRepository extends TemporalEntityRepository<InvestmentActionType> {
}
