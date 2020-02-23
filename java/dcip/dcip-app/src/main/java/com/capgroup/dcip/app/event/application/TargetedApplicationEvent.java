package com.capgroup.dcip.app.event.application;

import org.springframework.core.ResolvableTypeProvider;

public interface TargetedApplicationEvent<T> extends ApplicationEvent,
        ResolvableTypeProvider {
    T getTarget();
}
