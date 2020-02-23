package com.capgroup.dcip.infrastructure.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.capgroup.dcip.domain.identity.IdentityMapping;

@Repository
public interface IdentityMappingRepository extends CrudRepository<IdentityMapping, Long> {
	Optional<IdentityMapping> findByExternalIdAndEntityTypeIdAndDataSourceId(String externalId,long entityTypeId,
			long dataSourceId);

	@Query("select u from IdentityMapping u where u.entityTypeId = :entityTypeId and u.dataSourceId = :dataSourceId and u.externalId in :externalIds")
	Iterable<IdentityMapping> findByEntityTypeIdAndDataSourceIdAndExternalIdIn(@Param("externalIds") Iterable<String> externalIds,
			@Param("entityTypeId") long entityTypeId, @Param("dataSourceId") long dataSourceId);
	
	Optional<IdentityMapping> findByInternalIdAndEntityTypeIdAndDataSourceId(long internalId, long entityTypeId,
			long dataSourceId);
}
