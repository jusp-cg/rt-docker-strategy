package com.capgroup.dcip.infrastructure.repository;

import com.capgroup.dcip.domain.entity.EntityProperty;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EntityPropertyRepository extends TemporalEntityRepository<EntityProperty> {
    /**
     * Properties follow rule of Profile->User->InvestmentUnit hierarchy. Properties lower in the hierarchy have a
     * higher priority than those higher
     */
    @Query("select p from EntityProperty p where ((:path is not null and lower(p.key) like lower(cast(:path as string))) OR (:key is " +
            "not null and lower(p.key) = lower(cast(:key as string))) or (:key is null and :path is null)) and " +
            "(p.entity.id = :profileId or " +
            "(p.entity.id = (select p1.user.id from Profile as p1 where p1.id = :profileId) and p.key not in " +
            "(select p2.key from EntityProperty p2 where p2.entity.id = :profileId)) " +
            "or (p.entity.id = (select p3.user.investmentUnit.id from Profile p3 where p3.id = :profileId) " +
            "and p.key not in (select p4.key from EntityProperty p4 where p4.entity.id = :profileId) " +
            "and p.key not in (select p5.key from EntityProperty p5 where p5.entity.id = " +
            "(select p6.user.id from Profile p6 where p6.id = :profileId))))")
    Iterable<EntityProperty> findAllForProfile(@Param("profileId") long profileId,
                                               @Param("path") String path, @Param("key") String key);

    /**
     * Properties follow rule of User->InvestmentUnit hierarchy. Properties lower in the hierarchy have a
     * higher priority than those higher
     */
    @Query("select p from EntityProperty as p where ((:path is not null and lower(p.key) like lower(cast(:path as string))) OR (:key is" +
            " not null and lower(p.key) = lower(cast(:key as string))) or (:path is null and :userId is null)) and " +
            "(p.entity.id = :userId or " +
            "(p.entity.id = (select u1.investmentUnit.id from User u1 where u1.id = :userId) " +
            "and p.key not in (select p2.key from EntityProperty p2 where p2.entity.id = :userId)))")
    Iterable<EntityProperty> findAllForUser(@Param("userId") long userId, @Param("path") String path,
                                            @Param("key") String key);

    @Query("select p from EntityProperty as p where ((:path is not null and lower(p.key) like lower(cast(:path as string))) OR (:key is" +
            " not null and lower(p.key) = lower(cast(:key as string))) or (:path is null and :key is null)) and p.entity.id = :entityId")
    Iterable<EntityProperty> findAllForEntity(long entityId, @Param("path") String path, @Param("key") String key);

    @Query("select p from EntityProperty as p where ((:path is not null and lower(p.key) like lower(cast(:path as string))) OR (:key is" +
            " not null and lower(p.key) = lower(cast(:key as string))) or (:path is null and :key is null)) and p.entity.id is null")
    Iterable<EntityProperty> findAllDefaults(@Param("path") String path, @Param("key") String key);
}