package com.capgroup.dcip.infrastructure.repository;

import com.capgroup.dcip.domain.entity.Temporal;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Provides methods for querying the history of entities
 */
@NoRepositoryBean
public interface TemporalRepository<TTemporal extends Temporal, TKey> {
    Iterable<TTemporal> findAllVersions(TKey key, long investmentUnitId);
}
