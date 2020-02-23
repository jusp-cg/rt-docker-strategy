package com.capgroup.dcip.app.alert.service;

import com.capgroup.dcip.app.alert.AlertTypeModel;
import com.capgroup.dcip.app.alert.model.AlertActionTypeModel;

public interface AlertMetaDataService {
    Iterable<AlertTypeModel> findAllAlertTypes();

    AlertTypeModel findAlertTypeById(long id);

    Iterable<AlertActionTypeModel> findAllAlertActionTypes();

    Iterable<AlertActionTypeModel> findAlertActionTypes(long id);
}
