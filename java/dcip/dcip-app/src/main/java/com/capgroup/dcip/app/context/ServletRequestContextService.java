package com.capgroup.dcip.app.context;

import com.capgroup.dcip.domain.identity.Profile;
import com.capgroup.dcip.infrastructure.repository.ProfileRepository;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;

/**
 * Request specific properties
 */
@Service
public class ServletRequestContextService implements RequestContextService {

    private ProfileRepository profileRepository;
    private TimeBasedGenerator uuidGenerator = Generators.timeBasedGenerator();

    @Autowired
    public ServletRequestContextService(ProfileRepository repository) {
        this.profileRepository = repository;
    }

    @Override
    public String eventType() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        return request.getMethod() + " " + request.getRequestURI();
    }

    @Override
    public Profile currentProfile() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes();

        HttpServletRequest request = attributes.getRequest();
        String profileId = request.getHeader("ProfileId");

        if (!StringUtils.hasText(profileId)) {
            // if no profile id was passed in, get the default profileid for the user
            ProfileRepository.ExpressionBuilder builder = new ProfileRepository.ExpressionBuilder();
            BooleanExpression expression =
                    builder.hasUserInitials(this.authentication().getName()).and(builder.hasDefault(true));

            return profileRepository.findOne(expression).orElseThrow(() ->
                    new RuntimeException("No default profile configured for " + authentication().getName()));
        } else {
            long value = 0l;
            try {
                value = Long.parseLong(profileId);
            } catch (Throwable exc) {
                throw new RuntimeException(
                        "Invalid profileId:" + profileId + " for user:" + authentication() == null ? "unknown"
                                : authentication().getName(),
                        exc);
            }

            return profileRepository.findById(value).orElseThrow(() -> new IllegalArgumentException("Unknown " +
                    "profile:" + profileId + " for user:" + authentication().getName()));
        }
    }

    @Override
    public UUID eventId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes();

        // try getting the value from the request
        HttpServletRequest request = attributes.getRequest();
        String correlationId = request.getHeader("CorrelationId");

        if (correlationId != null) {
            try {
                return UUID.fromString(correlationId);
            } catch (Throwable exc) {
                throw new RuntimeException(
                        "Invalid correlation Id:" + correlationId + " for user:" + authentication() == null ? "unknown"
                                : authentication().getName(),
                        exc);
            }
        }

        // if no value in the request - try retrieving one that has already been created
        // for this request
        UUID value = (UUID) request.getAttribute("CorrelationId");
        if (value != null) {
            MDC.put("CorrelationId", value.toString());
            return value;
        }

        // no UUID has yet to be created
        value = uuidGenerator.generate();
        request.setAttribute("CorrelationId", value);
        MDC.put("CorrelationId", value.toString());

        return value;
    }

    @Override
    public Long dataSourceId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes();

        HttpServletRequest request = attributes.getRequest();
        return Optional.ofNullable(request.getHeader("DataSourceId"))
                .map(x->Long.parseLong(x)).orElse(null);
    }

    @Override
    public Authentication authentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
