package com.capgroup.dcip.app.context;

import java.util.UUID;

import org.springframework.security.core.Authentication;

import com.capgroup.dcip.domain.identity.Profile;

/***
 * Service that provides information about the request
 */
public interface RequestContextService {

	Profile currentProfile();

	Authentication authentication();

	String eventType();
	
	UUID eventId();

	Long dataSourceId();
}
