package com.capgroup.dcip.infrastructure.repository.thesis;

import com.capgroup.dcip.domain.entity.TemporalEntity;
import com.capgroup.dcip.domain.thesis.ThesisPoint;
import com.capgroup.dcip.infrastructure.repository.TemporalEntityRepository;
import com.capgroup.dcip.infrastructure.repository.TemporalRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.EnumSet;

/**
 * Queries for reading/writing ThesisPoints to a database
 */
@Repository
public interface ThesisPointRepository extends TemporalEntityRepository<ThesisPoint>, QuerydslPredicateExecutor<ThesisPoint>,
        TemporalRepository<ThesisPoint, Long> {

    @Query("select tp from ThesisPoint tp where tp not in (select e.childThesisPoint from ThesisEdge e) and "
            + " tp not in (select e.parentThesisPoint from ThesisEdge e)")
    Iterable<ThesisPoint> findUnusedThesisPoints();

    @EntityGraph(attributePaths = {"event.profile"})
    @Query("select tp from ThesisPoint tp where (:profileId is null or tp.event.profile.id = :profileId) " +
            "and (COALESCE(:statuses, null) is null or status in :statuses)")
    Iterable<ThesisPoint> findAll(Long profileId, EnumSet<TemporalEntity.Status> statuses);
}
