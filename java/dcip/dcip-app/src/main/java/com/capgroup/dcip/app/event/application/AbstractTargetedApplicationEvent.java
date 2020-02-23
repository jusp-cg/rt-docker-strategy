package com.capgroup.dcip.app.event.application;

import lombok.Getter;
import org.springframework.core.ResolvableType;

public class AbstractTargetedApplicationEvent<T> extends AbstractApplicationEvent
        implements TargetedApplicationEvent<T> {

    @Getter
    T target;

    protected AbstractTargetedApplicationEvent(T target) {
        this.target = target;
    }

    @Override
    public ResolvableType getResolvableType() {
        return ResolvableType.forClassWithGenerics(getClass(),
                ResolvableType.forInstance(target));
    }
}
