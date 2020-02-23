package com.capgroup.dcip.app.relationship;

import com.capgroup.dcip.app.entity.TemporalEntityMapper;
import com.capgroup.dcip.domain.relationship.Relationship;

import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

import java.util.ArrayList;
import java.util.List;

/**
 * responsible for mapping between a Relationship (domain object) and
 * RelationshipModel (DTO), and vice-versa
 */
@Mapper(config = TemporalEntityMapper.class)
public abstract class RelationshipMapper {

	/**
	 * Mapping between a Relationship (domain object) and RelationshipModel (DTO)
	 */
	@Mappings({ @Mapping(target = "relationshipTypeId", source = "relationshipType.id"),
			@Mapping(target = "firstEntityId", source = "entityId1"),
			@Mapping(target = "secondEntityId", source = "entityId2"),
			@Mapping(target = "name", source = "relationshipType.roleEntityType2.entityType.name")})
	public abstract RelationshipModel map(Relationship relationship);

	@Mappings({ @Mapping(target = "relationshipType.id", source = "relationshipTypeId"),
			@Mapping(target = "entityId1", source = "firstEntityId"),
			@Mapping(target = "entityId2", source = "secondEntityId") })
	public abstract Relationship map(RelationshipModel relationshipModel);

	@InheritConfiguration(name = "map")
	public abstract void updateRelationship(RelationshipModel relationshipModel,
			@MappingTarget Relationship relationship);

	public Iterable<RelationshipModel> map(Iterable<Relationship> relationships) {
		List<RelationshipModel> relationshipModelList = new ArrayList<>();
		relationships.forEach(r -> relationshipModelList.add(map(r)));

		return relationshipModelList;
	}
}
