package com.capgroup.dcip.infrastructure.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.capgroup.dcip.domain.data.DataSourceConnection;

@Repository
public interface DataSourceConnectionRepository extends CrudRepository<DataSourceConnection, Long> {

}
