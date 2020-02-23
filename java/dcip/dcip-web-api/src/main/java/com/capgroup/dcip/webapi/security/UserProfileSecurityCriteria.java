package com.capgroup.dcip.webapi.security;

import javax.servlet.http.HttpServletRequest;

/**
 * Criteria that determines if the UserProfileSecurityFilter should check that the profileId/user initials match
 */
public interface UserProfileSecurityCriteria {
    /**
     * If the return value is false - then the UserProfileSecurityFilter will validate the profile id/user initials
     */
    boolean matches(HttpServletRequest request, Long dataSourceId);
}
