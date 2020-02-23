package com.capgroup.dcip.infrastructure.repository.alert;

import com.capgroup.dcip.domain.alert.AlertSpecification;
import com.capgroup.dcip.domain.alert.QAlertSpecification;
import com.capgroup.dcip.domain.identity.Profile;
import com.capgroup.dcip.infrastructure.repository.TemporalEntityRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AlertSpecificationRepository extends TemporalEntityRepository<AlertSpecification>,
        QuerydslPredicateExecutor<AlertSpecification> {

    boolean existsByEventProfileAndNameIgnoreCaseAndActiveIsTrue(Profile profile, String name);

    @Query("select s from AlertSpecification s where (COALESCE(:ids, null) is null OR id in (:ids)) AND " +
            "(:active is null OR active = :active) AND (cast(:targetDate as date) is null OR targetDate is null OR targetDate >= :targetDate)")
    @EntityGraph(attributePaths = {"alertCriteria", "event"})
    Iterable<AlertSpecification> findAllBy(List<Long> ids, Boolean active, LocalDate targetDate);
}
