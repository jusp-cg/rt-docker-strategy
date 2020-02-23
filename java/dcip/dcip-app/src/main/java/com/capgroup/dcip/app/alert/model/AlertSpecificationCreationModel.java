package com.capgroup.dcip.app.alert.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlertSpecificationCreationModel {
    private AlertSpecificationModel specification;
    private List<AlertSubscriptionModel> subscriptions;
}
