package com.capgroup.dcip.app.alert.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AlertCriteriaModel {
    private long id;
    private long seriesId;
    private long companyId;
    private String operator;
    private BigDecimal value;
    private AlertCriteriaModel and;
    private AlertCriteriaModel or;
    private AlertCriteriaModel not;
}
