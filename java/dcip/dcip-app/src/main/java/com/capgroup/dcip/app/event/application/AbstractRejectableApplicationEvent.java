package com.capgroup.dcip.app.event.application;

import org.springframework.context.ApplicationEventPublisher;

public class AbstractRejectableApplicationEvent<T, E extends AbstractRejectableApplicationEvent<T, E>> extends AbstractRejectable<E> implements ApplicationEvent {

    String name;
    ApplicationEvent cause;

    @Override
    public ApplicationEvent getCause() {
        return cause;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void publish(ApplicationEventPublisher eventPublisher) {
        eventPublisher.publishEvent(this);
        complete();
    }
}
