package com.capgroup.dcip.app.event.application;

import lombok.Getter;
import org.springframework.context.ApplicationEventPublisher;

public abstract class AbstractApplicationEvent implements ApplicationEvent {
    @Getter
    private String name;
    @Getter
    private ApplicationEvent cause;

    public AbstractApplicationEvent(String name, ApplicationEvent cause) {
        this.name = name;
        this.cause = cause;
    }

    public AbstractApplicationEvent(String name) {
        this(name, null);
    }

    public AbstractApplicationEvent(){
    }

    @Override
    public void publish(ApplicationEventPublisher eventPublisher) {
        eventPublisher.publishEvent(this);
    }
}
