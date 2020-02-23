package com.capgroup.dcip.infrastructure.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.capgroup.dcip.domain.event.Event;

/**
 * JPA Repository for CRUD operations on an Event
 */
@Repository
public interface EventRepository extends CrudRepository<Event, UUID> {

}