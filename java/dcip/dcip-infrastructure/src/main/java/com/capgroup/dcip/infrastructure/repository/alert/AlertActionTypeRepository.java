package com.capgroup.dcip.infrastructure.repository.alert;

import com.capgroup.dcip.domain.alert.AlertActionType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * provides CRUD operations for an AlertActionType
 */
@Repository
public interface AlertActionTypeRepository extends CrudRepository<AlertActionType, Long> {
}
