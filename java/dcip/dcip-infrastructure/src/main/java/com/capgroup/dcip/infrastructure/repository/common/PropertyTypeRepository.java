package com.capgroup.dcip.infrastructure.repository.common;

import com.capgroup.dcip.domain.common.PropertyType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PropertyTypeRepository extends CrudRepository<PropertyType, Long> {
    Optional<PropertyType> findByKeyIgnoreCase(String key);
}
