package com.capgroup.dcip.infrastructure.repository;

import com.capgroup.dcip.domain.entity.Entity;
import com.capgroup.dcip.domain.entity.QEntity;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Interface for accessing Entities from the DB
 */
@Repository
public interface EntityRepository<TEntity extends Entity> extends CrudRepository<TEntity, Long> {
    List<TEntity> findByEventProfileId(long id);

    default TEntity findByIdUnchecked(Long id){
        return findById(id).orElseThrow(()->new RuntimeException());
    }

    class ExpressionBuilder {
        protected QEntity entity;

        public ExpressionBuilder() {
            this(QEntity.entity);
        }

        public ExpressionBuilder(QEntity entity) {
            this.entity = entity;
        }

        public BooleanExpression hasInvestmentUnit(Long investmentUnitId) {
            return investmentUnitId == null ? Expressions.TRUE.eq(true) :
                    entity.event.investmentUnit.id.eq(investmentUnitId);
        }

        public BooleanExpression hasProfile(Long profileId) {
            return profileId == null ? Expressions.TRUE.eq(true) : entity.event.profile.id.eq(profileId);
        }

        public BooleanExpression inIds(List<Long> ids) {
            return (ids == null || ids.size() == 0) ? Expressions.TRUE.eq(true) :
                    entity.id.in(ids);
        }
    }
}
