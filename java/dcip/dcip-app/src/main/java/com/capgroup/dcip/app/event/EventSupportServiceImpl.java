package com.capgroup.dcip.app.event;

import com.capgroup.dcip.app.context.RequestContextService;
import com.capgroup.dcip.domain.event.Event;
import com.capgroup.dcip.domain.identity.Profile;
import com.capgroup.dcip.infrastructure.repository.EventRepository;
import com.fasterxml.uuid.Generators;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Responsible for creating an Event when a non-readonly transaction occurs, and
 * providing access to that event
 */
@Configuration
@Aspect
@Slf4j
public class EventSupportServiceImpl implements EventSupportService {

    private EventRepository eventRepository;
    private RequestContextService requestContext;

    @Autowired
    public EventSupportServiceImpl(EventRepository eventRepository, RequestContextService requestContext) {
        this.eventRepository = eventRepository;
        this.requestContext = requestContext;
    }

    @Override
    public Event getCurrentEvent() {
        return (Event) TransactionSynchronizationManager.getResource(getClass().getName());
    }

    /**
     * Invoked before a method with the @Transactional annotation
     */
    @Before("execution(@org.springframework.transaction.annotation.Transactional * *(..)) && @annotation(transaction)")
    public void onTransaction(Transactional transaction) throws Throwable {

        if (log.isDebugEnabled())
            log.debug("processing transactional annotation");

        // no need to create an event if its a queryAll only transaction
        if (transaction.readOnly()) {
            return;
        }

        // register the event with the synchronization manager
        if (!TransactionSynchronizationManager.hasResource(getClass().getName())) {

            // create an event
            Profile profile = requestContext.currentProfile();
            Event event = new Event(Generators.timeBasedGenerator().generate(), requestContext.eventType(), profile,
                    requestContext.authentication().getName(),
                    requestContext.dataSourceId(), requestContext.eventId());
            event = eventRepository.save(event);

            if (log.isDebugEnabled())
                log.debug(
                        "Creating new transaction for profile:{}, event:{}, user:{} ", profile,
                        requestContext.eventType(),
                        requestContext.authentication().getName());

            TransactionSynchronizationManager.bindResource(getClass().getName(), event);
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCompletion(int status) {

                    TransactionSynchronizationManager
                            .unbindResourceIfPossible(EventSupportServiceImpl.this.getClass().getName());

                    if (log.isDebugEnabled())
                        log.debug("Transaction completed for profile:{} and event:{}", profile,
                                requestContext.eventType());
                }
            });
        }
    }
}
