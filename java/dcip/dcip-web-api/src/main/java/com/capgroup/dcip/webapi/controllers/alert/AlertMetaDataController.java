package com.capgroup.dcip.webapi.controllers.alert;

import com.capgroup.dcip.app.alert.model.AlertActionTypeModel;
import com.capgroup.dcip.app.alert.AlertTypeModel;
import com.capgroup.dcip.app.alert.service.AlertMetaDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/dcip/alert")
public class AlertMetaDataController {

    private AlertMetaDataService alertMetaDataService;

    @Autowired
    public AlertMetaDataController(AlertMetaDataService alertMetaDataService) {
        this.alertMetaDataService = alertMetaDataService;
    }

    @GetMapping("/types")
    public Iterable<AlertTypeModel> getAlertTypes() {
        return alertMetaDataService.findAllAlertTypes();
    }

    @GetMapping("/types/{id}")
    public AlertTypeModel getAlertType(@PathVariable("id") long id) {
        return alertMetaDataService.findAlertTypeById(id);
    }

    @GetMapping("/actions")
    public Iterable<AlertActionTypeModel> getAlertActionTypes(){
        return alertMetaDataService.findAllAlertActionTypes();
    }

    @GetMapping("/actions/{id}")
    public Iterable<AlertActionTypeModel> getAlertActionType(@PathVariable("id") long id){
        return alertMetaDataService.findAlertActionTypes(id);
    }

}
