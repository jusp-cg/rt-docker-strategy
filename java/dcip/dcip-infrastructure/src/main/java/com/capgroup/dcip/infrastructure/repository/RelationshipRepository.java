package com.capgroup.dcip.infrastructure.repository;

import com.capgroup.dcip.domain.entity.TemporalEntity;
import com.capgroup.dcip.domain.relationship.Relationship;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * API for reading in repositories. Need to remove the concept of Producer/Consumer and use roles
 */
@Transactional
public interface RelationshipRepository extends TemporalEntityRepository<Relationship> {

    @Query("select r from Relationship r where :consumerId = r.entityId1 and (:status is null OR r.status = :status)")
    Iterable<Relationship> findRelationsByConsumerIdAndStatus(@Param("consumerId") Long consumerId,
                                                              @Param("status") TemporalEntity.Status status);

    @Query("select r from Relationship r where (COALESCE(:ids, null) is null OR r.entityId1 in (:ids)) " +
            "and (:status is null OR r.status = :status)")
    Iterable<Relationship> findAllRelationsByConsumerIdAndStatus(@Param("status") TemporalEntity.Status status,
                                                                 @Param("ids") List<Long> ids);

    @Query("select r from Relationship r where :producerId = r.entityId2 and (:status is null OR r.status = :status)")
    Iterable<Relationship> findRelationsByProducerIdAndStatus(@Param("producerId") Long producerId,
                                                              @Param("status") TemporalEntity.Status status);

    @Query("select r from Relationship r where (:entityId = r.entityId2 or :entityId = r.entityId1) and (:status is " +
            "null OR r.status = :status)")
    Set<Relationship> findAllByEntityIdAndStatus(@Param("entityId") long entityId,
                                                 @Param("status") TemporalEntity.Status status);


}