package com.capgroup.dcip.app.alert.service;

import com.capgroup.dcip.app.data.SeriesService;
import com.capgroup.dcip.app.reference.company.CompanyService;
import com.capgroup.dcip.domain.alert.AlertCriteria;
import com.capgroup.dcip.domain.alert.AlertCriteriaEvaluation;
import com.capgroup.dcip.domain.alert.AlertSpecificationEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.RoundingMode;

@Component
public class AlertSpecificationEventMessageBuilderImpl
        implements AlertSpecificationEventMessageBuilder {
    CompanyService companyService;
    SeriesService seriesService;

    @Autowired
    public AlertSpecificationEventMessageBuilderImpl(CompanyService companyService,
                                                     SeriesService seriesService) {
        this.companyService = companyService;
        this.seriesService = seriesService;
    }

    public String build(AlertSpecificationEvent event) {
        if (event.getAlertCriteriaEvaluation() != null)
            return build(event.getAlertCriteriaEvaluation());
        else return "";
    }

    protected String build(AlertCriteriaEvaluation evaluation) {
        if (evaluation.getErrorMessage() != null) {
            return "#ERROR:" + evaluation.getErrorMessage();
        }

        AlertCriteria alertCriteria = evaluation.getAlertCriteria();
        StringBuilder stringBuilder = new StringBuilder();
        if (alertCriteria.getDataOperator() != null) {
            stringBuilder.append(companyService.findByIdOrElseThrow(alertCriteria.getCompanyId()).getShortName());
            stringBuilder.append(' ');
            stringBuilder.append(seriesService.findByIdOrElseThrow(alertCriteria.getSeriesId()).getName());
            stringBuilder.append(' ');
            stringBuilder.append(alertCriteria.getDataOperator().getValue());
            stringBuilder.append(' ');
            stringBuilder.append(alertCriteria.getDataValue().setScale(2, RoundingMode.HALF_UP));
            stringBuilder.append(" - Actual Value ");
            stringBuilder.append(evaluation.getSeriesDataValue().setScale(2, RoundingMode.HALF_UP));
        }
        if (evaluation.getRLogicalOperandEvaluation() != null) {
            stringBuilder.append("(");
            build(evaluation.getRLogicalOperandEvaluation());
            stringBuilder.append(")");
        }
        return stringBuilder.toString();
    }
}
