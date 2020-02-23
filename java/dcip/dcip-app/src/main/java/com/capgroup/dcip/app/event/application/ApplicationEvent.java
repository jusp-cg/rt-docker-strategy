package com.capgroup.dcip.app.event.application;

import org.springframework.context.ApplicationEventPublisher;

public interface ApplicationEvent {

    ApplicationEvent getCause();

    String getName();

    void publish(ApplicationEventPublisher eventPublisher);
}
