package com.capgroup.dcip.app.event;

import com.capgroup.dcip.domain.event.Event;

/**
 * Returns the current Event for the Transaction. If there is no valid
 * transaction or the transaction is readonly this current event will be null
 */
public interface EventSupportService {
	Event getCurrentEvent();
}
