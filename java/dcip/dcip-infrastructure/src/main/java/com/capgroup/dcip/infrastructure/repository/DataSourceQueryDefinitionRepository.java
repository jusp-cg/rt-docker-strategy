package com.capgroup.dcip.infrastructure.repository;

import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import com.capgroup.dcip.domain.data.DataSourceQueryDefinition;

@Repository
public interface DataSourceQueryDefinitionRepository extends TemporalEntityRepository<DataSourceQueryDefinition>,
		QuerydslPredicateExecutor<DataSourceQueryDefinition> {

}
