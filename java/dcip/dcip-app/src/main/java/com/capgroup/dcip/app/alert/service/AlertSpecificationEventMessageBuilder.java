package com.capgroup.dcip.app.alert.service;

import com.capgroup.dcip.domain.alert.AlertSpecificationEvent;

public interface AlertSpecificationEventMessageBuilder {
    String build(AlertSpecificationEvent event);
}
