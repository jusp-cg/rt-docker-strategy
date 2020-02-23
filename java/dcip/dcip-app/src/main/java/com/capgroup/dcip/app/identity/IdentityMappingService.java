package com.capgroup.dcip.app.identity;

import java.util.Map;
import java.util.Optional;
import java.util.OptionalLong;

import com.capgroup.dcip.domain.data.DataSource;
import com.capgroup.dcip.domain.entity.Entity;
import com.capgroup.dcip.domain.entity.EntityType;

public interface IdentityMappingService {

	OptionalLong internalIdentifier(String externalIdentifier, DataSource dataSource, EntityType entityType);

	Map<String, Long> internalIdentifiers(long dataSourceId, long entityType, Iterable<String> externalIds);
	
	Optional<String> externalIdentifier(long internalIdentifier, DataSource dataSource, EntityType entityType);
	
	Optional<String> externalIdentifier(long internalIdentifier, DataSource dataSource, long entityType);
	
	Optional<String> externalIdentifier(long internalIdentifier, long dataSourceId, long entityType);

	Optional<String> externalIdentifier(Entity entity, DataSource dataSource);

	OptionalLong internalIdentifier(String externalIdentifier, long dataSourceId, long entityTypeId);
}
