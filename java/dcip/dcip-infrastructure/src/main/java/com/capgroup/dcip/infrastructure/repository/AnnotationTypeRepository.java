package com.capgroup.dcip.infrastructure.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.capgroup.dcip.domain.annotation.AnnotationType;

@Repository
public interface AnnotationTypeRepository extends CrudRepository<AnnotationType, Long>{

}
