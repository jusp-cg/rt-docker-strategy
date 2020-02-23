package com.capgroup.dcip.infrastructure.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.capgroup.dcip.domain.canvas.WorkbenchResource;

@Repository
public interface WorkbenchResourceRepository extends CrudRepository<WorkbenchResource, Long> {

}
