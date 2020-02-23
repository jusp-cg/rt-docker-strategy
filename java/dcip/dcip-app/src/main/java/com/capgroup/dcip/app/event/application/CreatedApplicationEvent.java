package com.capgroup.dcip.app.event.application;

public class CreatedApplicationEvent<T> extends AbstractTargetedApplicationEvent<T> {

    public CreatedApplicationEvent(T target) {
        super(target);
    }
}
