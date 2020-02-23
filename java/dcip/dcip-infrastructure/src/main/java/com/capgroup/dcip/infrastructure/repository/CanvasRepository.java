package com.capgroup.dcip.infrastructure.repository;

import com.capgroup.dcip.domain.canvas.Canvas;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * JPA Repository for CRUD methods on a Canvas
 */
@Repository
public interface CanvasRepository extends TemporalEntityRepository<Canvas> {

    @Query("select c from Canvas c inner join c.canvasItems as ci where ci.entityId = :entityId and ci" +
            ".workbenchResource.id = :workbenchResourceId")
    Iterable<Canvas> findByCanvasItemsEntityId(@Param("entityId") long entityId,
                                               @Param("workbenchResourceId") long workbenchResourceId);

    @Query("select c from Canvas c where (:profileId is null or c.event.profile.id = :profileId) " +
            "and (:match is null or lower(c.description) = lower(cast(:match as string)) or lower(c.name) = lower(cast(:match as string)))")
    @EntityGraph(attributePaths = {"event"})
    Iterable<Canvas> findAllBy(Long profileId, String match);

    boolean existsByNameIgnoreCaseAndEventProfileId(String name, long profileId);
}
