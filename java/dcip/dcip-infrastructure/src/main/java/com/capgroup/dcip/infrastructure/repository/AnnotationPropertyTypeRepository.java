package com.capgroup.dcip.infrastructure.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.capgroup.dcip.domain.annotation.AnnotationPropertyType;

@Repository
public interface AnnotationPropertyTypeRepository extends CrudRepository<AnnotationPropertyType, Long>{

}
