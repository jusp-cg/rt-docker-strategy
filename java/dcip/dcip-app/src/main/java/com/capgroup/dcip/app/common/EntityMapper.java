package com.capgroup.dcip.app.common;

import org.mapstruct.Mapper;
import org.mapstruct.TargetType;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;

@Mapper
@UtilityEntityMapper
public class EntityMapper {
    @Autowired
    private EntityManager entityManager;

    @ToEntity
    public <T> T map(long id, @TargetType Class<T> entityClass) {
        return entityManager.find(entityClass, id);
    }
}
