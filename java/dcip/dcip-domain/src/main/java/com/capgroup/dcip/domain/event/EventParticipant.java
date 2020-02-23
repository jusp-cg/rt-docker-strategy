package com.capgroup.dcip.domain.event;

/**
 * An object that is associated with an event  
 */
public interface EventParticipant {

	Event getEvent();
	
	void setEvent(Event event);
}
