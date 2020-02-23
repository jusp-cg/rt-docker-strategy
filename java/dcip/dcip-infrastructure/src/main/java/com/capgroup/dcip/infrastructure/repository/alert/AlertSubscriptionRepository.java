package com.capgroup.dcip.infrastructure.repository.alert;

import com.capgroup.dcip.domain.alert.AlertSubscription;
import com.capgroup.dcip.domain.alert.QAlertSubscription;
import com.capgroup.dcip.infrastructure.repository.TemporalEntityRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * Repository for CRUD operations on AlertSubscriptions
 */
@Repository
public interface AlertSubscriptionRepository extends TemporalEntityRepository<AlertSubscription>,
        QuerydslPredicateExecutor<AlertSubscription> {

    Set<AlertSubscription> findAllByEntityIdAndActiveTrue(long entityId);

    Set<AlertSubscription> findAllByEntityIdInAndActiveTrue(List<Long> entityIds);

    /**
     * Builder for creating AlertSubscription queries
     */
    class ExpressionBuilder extends TemporalEntityRepository.ExpressionBuilder {
        QAlertSubscription subscription;

        public ExpressionBuilder(QAlertSubscription subscription) {
            super(subscription._super);
            this.subscription = subscription;
        }

        public ExpressionBuilder() {
            this(QAlertSubscription.alertSubscription);
        }

        public BooleanExpression hasEntity(Long entityId) {
            return entityId == null ? Expressions.TRUE.eq(true) : subscription.entityId.eq(entityId);
        }

        public BooleanExpression hasSpecification(Long specificationId){
            return specificationId == null ? Expressions.TRUE.eq(true) :
                    subscription.alertSpecification.id.eq(specificationId);
        }
    }
}
