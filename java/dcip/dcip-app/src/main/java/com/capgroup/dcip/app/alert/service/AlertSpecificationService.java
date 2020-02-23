package com.capgroup.dcip.app.alert.service;

import com.capgroup.dcip.app.alert.model.AlertSpecificationCreationModel;
import com.capgroup.dcip.app.alert.model.AlertSpecificationModel;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for CRUD level operations on an AlertSpecification
 */
public interface AlertSpecificationService {
    Iterable<AlertSpecificationModel> findAll(Boolean active, List<Long> alertSpecificationIds,
                                              LocalDate targetDate);

    AlertSpecificationModel findById(long id);

    AlertSpecificationModel create(AlertSpecificationModel alert);

    AlertSpecificationCreationModel create(AlertSpecificationCreationModel model);

    AlertSpecificationModel update(long marketDataId, AlertSpecificationModel alert);

    AlertSpecificationModel delete(long marketDataId);


}
