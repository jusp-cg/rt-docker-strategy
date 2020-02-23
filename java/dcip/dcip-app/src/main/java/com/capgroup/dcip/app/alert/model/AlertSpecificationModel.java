package com.capgroup.dcip.app.alert.model;

import com.capgroup.dcip.app.alert.model.AlertCriteriaModel;
import com.capgroup.dcip.app.common.LinkModel;
import com.capgroup.dcip.app.entity.TemporalEntityModel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO object that represents an AlertSpecification
 */
@Data
public class AlertSpecificationModel extends TemporalEntityModel {
    private String name;
    private boolean active;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate targetDate;
    private AlertCriteriaModel alertCriteria;
    private LinkModel subscriptions;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime activeTimestampChange;
}
