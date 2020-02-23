package com.capgroup.dcip.infrastructure.repository;

import com.capgroup.dcip.domain.entity.QTemporalEntity;
import com.capgroup.dcip.domain.entity.TemporalEntity;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.EnumSet;

/**
 * JPA Repository for CRUD operations on a TemporalEntity
 */
@NoRepositoryBean
public interface TemporalEntityRepository<TEntity extends TemporalEntity> extends EntityRepository<TEntity>{
    /**
     * Builder class for creating DSL TemporalEntity expressions
     */
    class ExpressionBuilder extends EntityRepository.ExpressionBuilder {

        QTemporalEntity temporalEntity;

        public ExpressionBuilder() {
            this(QTemporalEntity.temporalEntity);
        }

        public ExpressionBuilder(QTemporalEntity temporalEntity) {
            super(temporalEntity._super);
            this.temporalEntity = temporalEntity;
        }

        public BooleanExpression inStatus(EnumSet<TemporalEntity.Status> statuses) {
            return temporalEntity.status.in(statuses);
        }
    }
}
