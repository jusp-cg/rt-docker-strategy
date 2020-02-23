package com.capgroup.dcip.infrastructure.repository.alert;

import com.capgroup.dcip.domain.alert.AlertSpecificationEvent;
import com.capgroup.dcip.domain.alert.QAlertSpecificationEvent;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Set;

@Repository
public interface AlertSpecificationEventRepository extends CrudRepository<AlertSpecificationEvent, Long>,
        QuerydslPredicateExecutor<AlertSpecificationEvent> {

    /**
     * Find by EvaluationDate and Alert Specification
     */
    Set<AlertSpecificationEvent> findAllByEvaluationDateAndAlertSpecification_Id(LocalDateTime evaluationDate,
                                                                                 long specificationId);

    /**
     * Builder for creating queries for AlertEvents
     */
    class ExpressionBuilder {
        QAlertSpecificationEvent alert;

        public ExpressionBuilder() {
            alert = QAlertSpecificationEvent.alertSpecificationEvent;
        }

        public BooleanExpression hasProfile(Long profileId) {
            return profileId == null ? Expressions.TRUE.eq(true) :
                    alert.alertSpecification.event.profile.id.eq(profileId);
        }

        public BooleanExpression hasInvestmentUnit(Long investmentUnitId) {
            return investmentUnitId == null ? Expressions.TRUE.eq(true) :
                    alert.alertSpecification.event.investmentUnit.id.eq(investmentUnitId);
        }
    }
}
