package com.capgroup.dcip.infrastructure;

import javax.sql.DataSource;

import com.capgroup.dcip.util.ConverterUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest
@EntityScan(basePackages = "com.capgroup.dcip.domain")
@ComponentScan(basePackages = "com.capgroup.dcip.util")
@PropertySource("classpath:application.properties")
@EnableAutoConfiguration
@ActiveProfiles("integration-test")
@Import({DefaultFormattingConversionService.class, ConverterUtils.class})
public class InfrastructureTestConfig {

	@Value("${spring.datasource.url}")
	private String url;

	@Value("${spring.datasource.username}")
	private String username;

	@Value("${spring.datasource.password}")
	private String password;

	@Bean
	public DataSource dataSource() {
		return DataSourceBuilder.create().url(url).username(username).password(password).build();
	}
}
