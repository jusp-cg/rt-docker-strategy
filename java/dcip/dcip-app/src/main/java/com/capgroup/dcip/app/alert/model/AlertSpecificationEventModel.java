package com.capgroup.dcip.app.alert.model;

import com.capgroup.dcip.app.alert.model.AlertCriteriaEvaluationModel;
import com.capgroup.dcip.app.common.LinkModel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AlertSpecificationEventModel {
    UUID id;

    LocalDateTime createdTimestamp;
    LinkModel<Long> alertSpecification;
    AlertCriteriaEvaluationModel alertCriteriaEvaluation;
    Boolean evaluationResult;
    LocalDateTime evaluationDate;
    LocalDate targetDate;
    String name;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    public LocalDateTime getCreatedTimestamp() {
        return createdTimestamp;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public LocalDate getTargetDate(){
        return targetDate;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    public LocalDateTime getEvaluationDate() {
        return evaluationDate;
    }
}
