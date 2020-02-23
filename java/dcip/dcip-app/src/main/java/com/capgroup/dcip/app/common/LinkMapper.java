package com.capgroup.dcip.app.common;

import com.capgroup.dcip.domain.entity.EntityType;
import com.capgroup.dcip.domain.entity.EntityTypeService;
import com.capgroup.dcip.domain.identity.Identifiable;
import com.capgroup.dcip.infrastructure.repository.EntityTypeRepository;
import org.hibernate.Hibernate;
import org.mapstruct.Mapper;
import org.mapstruct.TargetType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Utility class for creating LinkModels from Entities (or entity ids) and vice-versa
 */
@UtilityLinkMapper
@Mapper
public abstract class LinkMapper {

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private EntityTypeService entityTypeService;
    @Autowired
    private LinkMapperHelper linkMapperHelper;

    @ToLongLink
    public LinkModel<Long> map(long entityId) {
        return map(entityId, null);
    }

    @ToLongLink
    public <T extends Identifiable<Long>> LinkModel<Long> mapEntity(T source) {
        return Optional.ofNullable(source).flatMap(s -> entityTypeService.findEntityTypeForClass(Hibernate.getClass(source)))
                .map(entityType -> map(source.getId(), entityType, null)).orElse(null);
    }

    @ToUUIDLink
    public <T extends Identifiable<UUID>> LinkModel<UUID> mapUUIDEntity(T source) {
        return Optional.ofNullable(source).flatMap(s -> entityTypeService.findEntityTypeForClass(Hibernate.getClass(source)))
                .map(entityType -> map(source.getId(), entityType, null)).orElse(null);
    }

    public <T extends Identifiable<UUID>> LinkModel<UUID> map(UUID id, Class<?> type) {
        return map(id, entityTypeService.findEntityTypeForClass(type).get(), null);
    }

    public <T extends Identifiable<Long>> List<LinkModel<Long>> mapAll(Iterable<T> sources) {
        return StreamSupport.stream(sources.spliterator(), false).map(this::mapEntity).collect(Collectors.toList());
    }

    @ToEntity
    public <T> T map(LinkModel<Long> linkModel, @TargetType Class<T> entityClass) {
        return Optional.ofNullable(linkModel).map(i -> entityManager.find(entityClass, linkModel.getId())).orElse(null);
    }

    public LinkModel<Long> map(long entityId, String rel) {
        EntityType entityType = linkMapperHelper.findEntityTypeByEntityId(entityId).get();
        return map(entityId, entityType, rel);
    }

    public LinkModel<Long> map(long entityId, long entityTypeId, String rel) {
        return map(entityId, linkMapperHelper.findEntityTypeById(entityTypeId).get(), rel);
    }

    public LinkModel<Long> map(long entityId, EntityType entityType, String rel) {
        return new LinkModel(rel, entityType.getId(), entityId, entityTypeService.findResourceUrl(entityType,
                entityId).map(URL::toString).orElse(null));
    }

    public LinkModel<UUID> map(UUID entityId, EntityType entityType, String rel) {
        return new LinkModel(rel, entityType.getId(), entityId, entityTypeService.findResourceUrl(entityType,
                entityId).map(URL::toString).orElse(null));
    }


    /**
     * Required for the caching of the entity ids to entity types
     */
    @Service
    static class LinkMapperHelper {
        EntityTypeRepository entityTypeRepository;

        public LinkMapperHelper(EntityTypeRepository repository) {
            this.entityTypeRepository = repository;
        }

        @Cacheable("EntityTypeForEntityId")
        public Optional<EntityType> findEntityTypeByEntityId(long entityId) {
            return entityTypeRepository.findEntityTypeByEntityId(entityId);
        }

        @Cacheable("EntityType")
        public Optional<EntityType> findEntityTypeById(long entityTypeId) {
            return entityTypeRepository.findById(entityTypeId);
        }
    }
}
