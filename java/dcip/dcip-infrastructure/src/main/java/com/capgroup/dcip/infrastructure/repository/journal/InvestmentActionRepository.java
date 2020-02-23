package com.capgroup.dcip.infrastructure.repository.journal;

import com.capgroup.dcip.domain.journal.InvestmentAction;
import com.capgroup.dcip.infrastructure.repository.TemporalEntityRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface InvestmentActionRepository
        extends TemporalEntityRepository<InvestmentAction>, QuerydslPredicateExecutor<InvestmentAction> {

    @EntityGraph(attributePaths = {"event", "investmentTypesInActions",
            "accountsInActions", "investmentActionType", "analystProfile", "partnersInInvestmentAction",
            "investmentReasonInInvestmentAction", "partnersInInvestmentAction.partner",
            "investmentReasonInInvestmentAction.investmentReason"}, type = EntityGraph.EntityGraphType.FETCH)
    Iterable<InvestmentAction> findAllByEventProfileId(Long profileId);
}
