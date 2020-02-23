package com.capgroup.dcip.app.event.application;

import lombok.Getter;
import org.springframework.core.ResolvableType;

public class DeletedApplicationEvent<T> extends AbstractRejectableApplicationEvent<T, DeletedApplicationEvent<T>>
        implements TargetedApplicationEvent<T> {

    @Getter
    T target;

    public DeletedApplicationEvent(T target) {
        this.target = target;
    }

    @Override
    public ResolvableType getResolvableType() {
        return ResolvableType.forClassWithGenerics(getClass(),
                ResolvableType.forInstance(target));
    }
}
