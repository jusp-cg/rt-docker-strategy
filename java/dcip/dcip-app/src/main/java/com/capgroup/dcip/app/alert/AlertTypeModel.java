package com.capgroup.dcip.app.alert;

import com.capgroup.dcip.app.alert.model.AlertActionTypeModel;
import lombok.Data;

import java.util.List;

@Data
public class AlertTypeModel {
    private long id;
    private String name;
    private String description;
    private List<AlertActionTypeModel> alertActionTypes;
}
