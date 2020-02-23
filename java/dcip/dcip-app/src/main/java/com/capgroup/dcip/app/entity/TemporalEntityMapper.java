package com.capgroup.dcip.app.entity;

import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.MappingInheritanceStrategy;
import org.mapstruct.Mappings;

import com.capgroup.dcip.domain.entity.TemporalEntity;

/**
 * Maps between a com.capgroup.dcip.temporal.TemporalEntity to a TemporalEntityModel
 */
@MapperConfig(mappingInheritanceStrategy = MappingInheritanceStrategy.AUTO_INHERIT_FROM_CONFIG )
public interface TemporalEntityMapper {

	@Mappings({ @Mapping(target = "id", ignore = true), @Mapping(target = "validPeriod", ignore = true),
			@Mapping(target = "versionId", ignore = true), @Mapping(target="status", ignore=true) })
	TemporalEntity mapToEntity(TemporalEntityModel entityModel);

	@Mappings({ @Mapping(target = "profileId", source = "event.profile.id"),
			@Mapping(target = "entityTypeId", source = "entityType.id"),
			@Mapping(target = "initials", source = "event.profile.user.initials"),
			@Mapping(target = "role", source = "event.profile.role.name"),
			@Mapping(target="modifiedTimestamp", source="event.createdTimestamp")})
	TemporalEntityModel mapToModel(TemporalEntity entity);
}
