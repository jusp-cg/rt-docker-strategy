package com.capgroup.dcip.app.relationship;

import com.capgroup.dcip.app.common.LinkModel;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;

import com.capgroup.dcip.domain.entity.EntityType;
import com.capgroup.dcip.domain.entity.EntityTypeService;
import com.capgroup.dcip.domain.relationship.Relationship;

@Mapper
public abstract class RelationshipLinkMapper {

	@Autowired
	EntityTypeService entityTypeService;

	@Mappings({ @Mapping(target = "id", source = "entityId2"),
			@Mapping(target = "typeId", source = "relationshipType.roleEntityType2.entityType.id") })
	public abstract LinkModel<Long> map(Relationship relationship);

	@AfterMapping
	public void afterMapping(Relationship src, @MappingTarget LinkModel<Long> target) {
		EntityType entityType = src.getRelationshipType().getRoleEntityType2().getEntityType();
		entityTypeService.findResourceUrl(entityType, src.getEntityId2()).ifPresent(url -> {
			target.setRef(url.toString());
		});
	}

	public abstract Iterable<LinkModel<Long>> map(Iterable<Relationship> domain);
}
