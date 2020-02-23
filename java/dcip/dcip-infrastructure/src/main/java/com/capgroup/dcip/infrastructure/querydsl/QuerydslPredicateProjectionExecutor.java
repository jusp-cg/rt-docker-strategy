package com.capgroup.dcip.infrastructure.querydsl;

import com.querydsl.core.types.Predicate;

import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import com.querydsl.core.types.FactoryExpression;

/**
 * Extends the Spring DSL executor to allow for projections
 */
public interface QuerydslPredicateProjectionExecutor<T> extends QuerydslPredicateExecutor<T>{
	<P> Iterable<P> findAll(Predicate predicate, FactoryExpression<P> factoryExpression);
}
