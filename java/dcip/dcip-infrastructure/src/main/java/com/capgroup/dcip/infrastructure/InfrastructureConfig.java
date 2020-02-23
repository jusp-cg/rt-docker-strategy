package com.capgroup.dcip.infrastructure;

import com.capgroup.dcip.infrastructure.querydsl.QuerydslJpaRepositoryFactoryBean;
import com.capgroup.dcip.util.data.DataSourceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.sql.DataSource;

/**
 * Spring configuration for JPA repositories
 */
@Configuration
@EnableJpaRepositories(repositoryFactoryBeanClass = QuerydslJpaRepositoryFactoryBean.class)
@PropertySource("classpath:application.properties")
public class InfrastructureConfig {

	@Autowired
	DataSourceManager dataSourceManager;

	@Primary
	@Bean
	public DataSource defaultDatabase() {
		return dataSourceManager.dataSource("spring");
	}
}
