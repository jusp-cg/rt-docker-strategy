package com.capgroup.dcip.infrastructure.repository.thesis;

import com.capgroup.dcip.domain.thesis.ThesisEdge;
import com.capgroup.dcip.infrastructure.repository.TemporalEntityRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * Interface for querying ThesisEdges
 */
@Repository
public interface ThesisEdgeRepository extends TemporalEntityRepository<ThesisEdge> {
    /**
     * Find ThesisEdges that have a Parent/Child that matches the ThesisPointId (optional) and optional filter of
     * ThesisId
     */
    @Query("select thesisEdge from ThesisEdge thesisEdge where (:thesisPointId = null OR (thesisEdge.childThesisPoint" +
            ".id = :thesisPointId OR " +
            "thesisEdge.parentThesisPoint.id = :thesisPointId)) AND (:thesisId = null OR thesisEdge.thesis.id = :thesisId)")
    Set<ThesisEdge> findAllBy(Long thesisId, Long thesisPointId);
}
