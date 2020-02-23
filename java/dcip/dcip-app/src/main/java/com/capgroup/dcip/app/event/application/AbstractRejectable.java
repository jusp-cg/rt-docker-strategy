package com.capgroup.dcip.app.event.application;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AbstractRejectable<T extends AbstractRejectable<T>> implements Rejectable<T> {
    @Getter
    boolean rejected = false;
    boolean completed = false;
    List<Consumer<T>> rejectConsumers;
    List<Consumer<T>> completeConsumers;

    @Override
    public void reject() {
        if (!rejected && !completed) {
            rejected = true;
            if (rejectConsumers != null) {
                rejectConsumers.forEach(x -> x.accept((T) this));
            }
        }
    }

    @Override
    public T onReject(Consumer<T> consumer) {
        if (rejected) {
            consumer.accept((T) this);
            return (T) this;
        }

        if (rejectConsumers == null) {
            rejectConsumers = new ArrayList<>();
        }
        rejectConsumers.add(consumer);
        return (T) this;
    }

    @Override
    public T onComplete(Consumer<T> consumer) {
        if (completed) {
            consumer.accept((T) this);
            return (T) this;
        }

        if (completeConsumers == null) {
            completeConsumers = new ArrayList<>();
        }
        completeConsumers.add(consumer);
        return (T) this;
    }

    @Override
    public boolean isRejected() {
        return rejected;
    }

    @Override
    public boolean isCompleted() {
        return completed;
    }

    public void complete() {
        if (!rejected && !completed) {
            completed = true;
            if (completeConsumers != null) {
                completeConsumers.forEach(x -> x.accept((T) this));
            }
        }
    }
}
