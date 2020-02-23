package com.capgroup.dcip.infrastructure.repository;

import com.capgroup.dcip.domain.models.Model;
import com.capgroup.dcip.infrastructure.querydsl.QuerydslPredicateProjectionExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModelRepository extends TemporalEntityRepository<Model>, QuerydslPredicateProjectionExecutor<Model> {
    Iterable<Model> findByIdIn(List<Long> ids);
}
