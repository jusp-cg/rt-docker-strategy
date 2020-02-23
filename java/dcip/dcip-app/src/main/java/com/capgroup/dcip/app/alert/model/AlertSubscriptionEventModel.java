package com.capgroup.dcip.app.alert.model;

import com.capgroup.dcip.app.common.LinkModel;
import com.capgroup.dcip.domain.alert.AlertSubscriptionEventStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Data transfer object representing an event generated due to an AlertSubscription.
 */
@Data
public class AlertSubscriptionEventModel {
    private UUID id;
    private LinkModel<Long> alertSubscription;
    private LocalDateTime createdTimestamp;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime evaluationDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate targetDate;
    private String message;
    private String alertTypeName;
    private Boolean evaluationResult;
    private String alertSpecificationName;
    private AlertSubscriptionEventStatus.Status eventStatus;
    private List<AlertSubscriptionEventActionModel> alertSubscriptionEventActions;
    private boolean success;
    private AlertCriteriaEvaluationModel alertCriteriaEvaluation;
    private LinkModel<Long> entity;
    private LinkModel<Long> alertType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    public LocalDateTime getCreatedTimestamp(){
        return createdTimestamp;
    }

}
