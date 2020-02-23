package com.capgroup.dcip.infrastructure.repository.alert;

import com.capgroup.dcip.domain.alert.AlertType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * interface for reading/writing AlertTypes from the database
 */
@Repository
public interface AlertTypeRepository extends CrudRepository<AlertType, Long> {
}
