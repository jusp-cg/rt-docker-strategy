package com.capgroup.dcip.infrastructure.repository.alert;

import com.capgroup.dcip.domain.alert.AlertSubscriptionEvent;
import com.capgroup.dcip.domain.alert.QAlertSubscriptionEvent;
import com.capgroup.dcip.infrastructure.querydsl.QuerydslPredicateProjectionExecutor;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository for CRUD operations on an AlertSubscrtiptionEvent
 */
@Repository
public interface AlertSubscriptionEventRepository extends CrudRepository<AlertSubscriptionEvent, UUID>,
        QuerydslPredicateProjectionExecutor<AlertSubscriptionEvent> {

    @Query("select ase from AlertSubscriptionEvent ase where (COALESCE(:entityIds, null) is null OR ase" +
            ".alertSubscription.entityId in" +
            " (:entityIds)) " +
            "AND (:profileId is null OR alertSubscription.event.profile.id = :profileId) " +
            "AND (COALESCE(:ids, null) is null OR id in (:ids)) " +
            "AND (cast(:createdSince as timestamp) is null OR alertSpecificationEvent.createdTimestamp > " +
            ":createdSince) " +
            "AND (cast(:createdTill as timestamp) is null OR alertSpecificationEvent.createdTimestamp < :createdTill)" +
            " " +
            "AND (cast(:evaluatedSince as timestamp) is null OR alertSpecificationEvent.evaluationDate >= " +
            ":evaluatedSince) " +
            "AND (cast(:evaluatedTill as timestamp) is null OR alertSpecificationEvent.evaluationDate <= " +
            ":evaluatedTill) " +
            "AND ((:evaluationResult is null OR (alertSpecificationEvent.evaluationResult is not null AND " +
            "alertSpecificationEvent.evaluationResult = :evaluationResult)) OR " +
            "(:failed is null OR :failed = false OR alertSpecificationEvent.evaluationResult IS NULL)) ")
    @EntityGraph(attributePaths = {"alertSubscription", "alertSubscriptionEventStatus",
            "alertSpecificationEvent.alertSpecification.alertCriteria", "alertSubscriptionEventActions"})
    Iterable<AlertSubscriptionEvent> findAllBy(@Param("entityIds") List<Long> entityIds,
                                               @Param("profileId") Long profileId,
                                               @Param("ids") List<UUID> ids,
                                               @Param("evaluationResult") Boolean evaluationResult,
                                               @Param("failed") Boolean failed,
                                               @Param("createdSince") LocalDateTime createdSince,
                                               @Param("createdTill") LocalDateTime createdTill,
                                               @Param("evaluatedSince") LocalDateTime evaluatedSince,
                                               @Param("evaluatedTill") LocalDateTime evaluatedTill);


    /**
     * Builder for creating AlertSubscriptionEvent queries
     */
    class ExpressionBuilder {
        QAlertSubscriptionEvent subscriptionEvent;

        public ExpressionBuilder(QAlertSubscriptionEvent subscription) {
            this.subscriptionEvent = subscription;
        }

        public ExpressionBuilder() {
            this(QAlertSubscriptionEvent.alertSubscriptionEvent);
        }

        public BooleanExpression inEntities(List<Long> entityIds) {
            return (entityIds == null || entityIds.isEmpty()) ? Expressions.TRUE.eq(true) :
                    subscriptionEvent.alertSubscription.entityId.in(entityIds);
        }

        public BooleanExpression hasProfile(Long profileId) {
            return profileId == null ? Expressions.TRUE.eq(true) :
                    subscriptionEvent.alertSubscription.event.profile.id.eq(profileId);
        }

        public BooleanExpression inIds(List<UUID> ids) {
            return (ids == null || ids.isEmpty()) ? Expressions.TRUE.eq(true) :
                    subscriptionEvent.id.in(ids);
        }

        public BooleanExpression isEvaluationResult(Boolean b) {
            return b == null ? Expressions.TRUE.eq(true) :
                    subscriptionEvent.alertSpecificationEvent.evaluationResult.isNotNull().and(subscriptionEvent.alertSpecificationEvent.evaluationResult.eq(b));
        }

        public BooleanExpression isSuccess() {
            return subscriptionEvent.alertSpecificationEvent.evaluationResult.isNotNull().and(subscriptionEvent.alertSpecificationEvent.evaluationResult.isTrue());
        }

        public BooleanExpression isErrors() {
            return subscriptionEvent.alertSpecificationEvent.evaluationResult.isNotNull().and(subscriptionEvent.alertSpecificationEvent.evaluationResult.isFalse());
        }

        public BooleanExpression isFailed() {
            return subscriptionEvent.alertSpecificationEvent.evaluationResult.isNull();
        }

        public BooleanExpression isFailed(Boolean b) {
            return (b == null || b == false) ? Expressions.TRUE.eq(true) :
                    subscriptionEvent.alertSpecificationEvent.evaluationResult.isNull();
        }

        public BooleanExpression createdSince(LocalDateTime ldt) {
            return ldt == null ? Expressions.TRUE.eq(true) :
                    subscriptionEvent.alertSpecificationEvent.createdTimestamp.gt(ldt);
        }

        public BooleanExpression createdTill(LocalDateTime ldt) {
            return ldt == null ? Expressions.TRUE.eq(true) :
                    subscriptionEvent.alertSpecificationEvent.createdTimestamp.lt(ldt);
        }

        public BooleanExpression evaluationSince(LocalDateTime ldt) {
            return ldt == null ? Expressions.TRUE.eq(true) :
                    subscriptionEvent.alertSpecificationEvent.evaluationDate.goe(ldt);
        }

        public BooleanExpression evaluationTill(LocalDateTime ldt) {
            return ldt == null ? Expressions.TRUE.eq(true) :
                    subscriptionEvent.alertSpecificationEvent.evaluationDate.loe(ldt);
        }
    }
}
