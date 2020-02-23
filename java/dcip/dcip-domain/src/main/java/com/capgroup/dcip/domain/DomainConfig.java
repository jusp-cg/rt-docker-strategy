package com.capgroup.dcip.domain;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Configuration for JPA:
 * <li>1</li> enables the auditing of timestamp/user
 * <li>2</li> denotes the location of the JPA entities
 */
@Configuration
@EnableJpaAuditing
@EntityScan
public class DomainConfig {

}
