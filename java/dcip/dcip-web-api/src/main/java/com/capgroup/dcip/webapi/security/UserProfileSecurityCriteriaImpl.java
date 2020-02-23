package com.capgroup.dcip.webapi.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Reads in from the property file 'security.filter' the json rules for applying to the request to determine
 * if the profile/user id should be validated
 */
@Component
public class UserProfileSecurityCriteriaImpl implements UserProfileSecurityCriteria {
    @Value("${security.filter}")
    private String securityFilter;
    private List<Filter> filters;

    @PostConstruct
    public void init() throws IOException {
        if (securityFilter == null) {
            filters = new ArrayList<>();
            return;
        }

        filters = new ObjectMapper().readValue(securityFilter, new TypeReference<List<Filter>>() {
        });
        filters = filters.stream().map(x -> x.init()).collect(Collectors.toList());
    }

    @Override
    public boolean matches(HttpServletRequest request, Long dataSourceId) {
        return filters.stream().anyMatch(x->x.matches(request, dataSourceId));
    }

    @Slf4j
    @Data
    public static class Filter {

        String method;
        String uri;
        Long dataSourceId;

        @JsonIgnore
        @ToString.Exclude
        Pattern methodPattern;

        @JsonIgnore
        @ToString.Exclude
        Pattern uriPattern;

        public boolean matches(HttpServletRequest request, Long dataSourceId) {
            if (log.isDebugEnabled()) {
                log.debug("validating request, dataSourceId:{}, method:{}, requestUri:{} against:{}",
                        dataSourceId, request.getMethod(), request.getRequestURI(), this);
            }

            // match on method
            if (methodPattern != null && !methodPattern.matcher(request.getMethod()).matches())
                return false;

            // match on uri
            if (uriPattern != null && !uriPattern.matcher(request.getRequestURI()).matches())
                return false;

            // match on datasource
            if (this.dataSourceId != null && !this.dataSourceId.equals(dataSourceId)) {
                return false;
            }

            return true;
        }

        public Filter init() {
            methodPattern = method == null ? null : Pattern.compile(method);
            uriPattern = uri == null ? null : Pattern.compile(uri);
            return this;
        }
    }

}
