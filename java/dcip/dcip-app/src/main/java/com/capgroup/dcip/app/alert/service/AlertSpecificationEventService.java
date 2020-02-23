package com.capgroup.dcip.app.alert.service;

import com.capgroup.dcip.app.alert.model.AlertSpecificationEventModel;

/**
 * Service for creating/updating/deleteing AlertSpecificationEvents
 */
public interface AlertSpecificationEventService {
    Iterable<AlertSpecificationEventModel> create(long alertSpecificationId,
                                                  Iterable<AlertSpecificationEventModel> models);
}
