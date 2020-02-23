package com.capgroup.dcip.cmps;

import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@SpringBootTest
@AutoConfigureWebClient
@PropertySource("classpath:application.properties")
//@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@ComponentScan(basePackages = "com.capgroup.dcip.cmps")
public class CmpsTestConfig {

}
