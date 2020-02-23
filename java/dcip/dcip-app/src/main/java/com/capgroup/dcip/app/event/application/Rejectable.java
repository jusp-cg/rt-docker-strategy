package com.capgroup.dcip.app.event.application;

import java.util.function.Consumer;

/**
 * Allows an item to be rejected before it has been completed
 */
public interface Rejectable<T extends Rejectable<T>> {
    void reject();

    T onReject(Consumer<T> event);

    T onComplete(Consumer<T> event);

    boolean isRejected();

    boolean isCompleted();

    void complete();
}
