package com.capgroup.dcip.app.alert.model;

import com.capgroup.dcip.app.common.LinkModel;
import com.capgroup.dcip.domain.alert.AlertCriteria;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data transfer object representing an Evaluation
 */
@Data
public class AlertCriteriaEvaluationModel {
    UUID id;
    LocalDateTime timeStamp;
    LinkModel<Long> alertCriteria;
    AlertCriteriaEvaluationModel rLogicalOperandEvaluation;
    Boolean dataOperatorEvaluation;
    Boolean evaluation;
    LocalDateTime seriesTimestamp;
    BigDecimal seriesDataValue;
    String errorMessage;
    Long companyId;
    String dataOperator;
    BigDecimal dataValue;
    AlertCriteria.LogicalOperator rLogicalOperator;
    Long seriesId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    public LocalDateTime getSeriesTimestamp(){
        return seriesTimestamp;
    }
}
