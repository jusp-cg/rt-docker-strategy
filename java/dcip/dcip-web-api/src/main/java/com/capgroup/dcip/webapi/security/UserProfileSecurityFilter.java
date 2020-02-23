package com.capgroup.dcip.webapi.security;

import com.capgroup.dcip.app.context.RequestContextService;
import com.capgroup.dcip.app.identity.ProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * Ensures that the profile id passed in through the header is valid for the user making the request
 */
@Component
@Order(1000)
@Slf4j
public class UserProfileSecurityFilter extends GenericFilterBean {

    ProfileService profileService;
    RequestContextService requestContextService;
    UserProfileSecurityCriteria criteria;

    @Autowired
    public UserProfileSecurityFilter(ProfileService profileService,
                                     RequestContextService requestContextService,
                                     UserProfileSecurityCriteria criteria) {
        this.profileService = profileService;
        this.requestContextService = requestContextService;
        this.criteria = criteria;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        log.debug("validating user/profile");

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        Long dataSourceId = Optional.ofNullable(httpRequest.getHeader("DataSourceId"))
                .map(x -> Long.parseLong(x)).orElse(null);

        if (!criteria.matches(httpRequest, dataSourceId)) {
            // validate the authentication
            Authentication authentication = requestContextService.authentication();
            if (!authentication.getName().contains("@")) {
                httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unexpected format for user initials:" + authentication.getName());
                return;
            }

            // validate the profile
            String pId = httpRequest.getHeader("ProfileId");
            if (pId == null){
                httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing ProfileId for user:" + authentication.getName());
                return;
            }
            Long profileId;
            try {
                profileId = Long.parseLong(pId);
            } catch (Exception exc) {
                httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid format of profileId:" + pId + ", for user:" + authentication.getName());
                return;
            }

            if (log.isDebugEnabled())
                log.debug("validating profileId:{} for user:{} and Initials:{}", profileId,
                        authentication.getName(),
                        authentication.getName().substring(0, authentication.getName().indexOf('@')));

            if (!profileService.isProfileValidForUser(profileId, authentication.getName().substring(0,
                    authentication.getName().indexOf('@')))) {
                httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Profile Id, is not valid for user");
                return;
            }
        }

        chain.doFilter(request, response);
    }
}
