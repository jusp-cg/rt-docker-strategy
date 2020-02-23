package com.capgroup.dcip.infrastructure.querydsl;

import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.Predicate;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.QuerydslJpaPredicateExecutor;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.querydsl.SimpleEntityPathResolver;

import javax.persistence.EntityManager;

/**
 * Extends the Spring/DSL repository to allow FactoryExpressions for projections
 */
public class QuerydslJpaRepositoryImpl<T> extends QuerydslJpaPredicateExecutor<T>
        implements QuerydslPredicateProjectionExecutor<T> {

    private static final EntityPathResolver resolver = SimpleEntityPathResolver.INSTANCE;

    public QuerydslJpaRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager, resolver, null);
    }

    @Override
    public <P> Iterable<P> findAll(Predicate predicate, FactoryExpression<P> factoryExpression) {
        return super.createQuery(predicate).select(factoryExpression).fetch();
    }
}
