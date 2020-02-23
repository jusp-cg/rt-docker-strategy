package com.capgroup.dcip.app.event.application;

public class ModifiedApplicationEvent<T> extends AbstractTargetedApplicationEvent<T> {

        public ModifiedApplicationEvent(T target) {
            super(target);
        }
}
