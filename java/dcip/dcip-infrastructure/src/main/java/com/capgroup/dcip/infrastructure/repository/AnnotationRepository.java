package com.capgroup.dcip.infrastructure.repository;

import java.util.stream.Stream;

import org.springframework.stereotype.Repository;

import com.capgroup.dcip.domain.annotation.Annotation;

@Repository
public interface AnnotationRepository extends TemporalEntityRepository<Annotation>{
	Stream<Annotation> findByEntityId(long id);
}
