package com.capgroup.dcip.app;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableCaching
@EnableTransactionManagement(order = Ordered.HIGHEST_PRECEDENCE)
public class AppConfig {

	@Value("${sAndPApi.userName}")
	String userName;
	@Value("${sAndPApi.password}")
	String password;

	@Bean
	public CacheManager cacheManager() {
		// configure and return an implementation of Spring's CacheManager SPI
		SimpleCacheManager cacheManager = new SimpleCacheManager();
		cacheManager
				.setCaches(Arrays.asList(new ConcurrentMapCache("Account"), 
						new ConcurrentMapCache("Company"),
						new ConcurrentMapCache("Listing", true),
						new ConcurrentMapCache("EntityType"),
						new ConcurrentMapCache("EntityTypeForEntityId"),
						new ConcurrentMapCache("UserProfileEntitlement")));
		return cacheManager;
	}

	@Bean
	public RestTemplate sAndPRestTemplate(RestTemplateBuilder builder){
		return builder.basicAuthentication(userName, password).build();
	}
}
