package com.capgroup.dcip.app.alert.model;

import com.capgroup.dcip.app.common.LinkModel;
import com.capgroup.dcip.app.entity.TemporalEntityModel;
import lombok.Data;

@Data
public class AlertSubscriptionModel extends TemporalEntityModel {
    boolean active;
    LinkModel<Long> entity;
    LinkModel<Long> alertType;
    LinkModel<Long> specification;
}
