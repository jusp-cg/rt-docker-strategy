package com.capgroup.dcip.app.alert.model;

import com.capgroup.dcip.app.common.LinkModel;
import lombok.Data;

import java.util.UUID;

@Data
public class AlertSubscriptionEventActionModel {
    UUID id;
    LinkModel<UUID> alertSubscriptionEvent;
    LinkModel<Long> alertActionType;
    LinkModel<Long> entity;
}
