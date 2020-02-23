package com.capgroup.dcip.domain.event;

import java.time.LocalDateTime;

/**
 * Defines the contract for qn obj3ct that has both a timestamp when it was
 * created and who created it
 */
public interface Auditable {
	LocalDateTime getCreatedTimestamp();

	String getCreatedBy();
}
