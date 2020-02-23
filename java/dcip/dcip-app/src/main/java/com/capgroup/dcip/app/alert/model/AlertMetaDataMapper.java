package com.capgroup.dcip.app.alert.model;

import com.capgroup.dcip.app.alert.AlertTypeModel;
import com.capgroup.dcip.app.alert.model.AlertActionTypeModel;
import com.capgroup.dcip.domain.alert.AlertActionType;
import com.capgroup.dcip.domain.alert.AlertType;
import org.mapstruct.Mapper;

/**
 * Mapping from domain meta data objects and DTO objects for Alerts (e.g. event status/alert type/actions/etc.)
 */
@Mapper
public interface AlertMetaDataMapper {

    AlertTypeModel map(AlertType definition);

    AlertActionTypeModel map(AlertActionType type);

    Iterable<AlertTypeModel> mapAllAlertTypes(Iterable<AlertType> alertTypes);

    Iterable<AlertActionTypeModel> mapAllAlertActionTypes(Iterable<AlertActionType> alertActionTypes);
}
