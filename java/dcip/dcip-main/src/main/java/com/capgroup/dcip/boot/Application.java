package com.capgroup.dcip.boot;

import com.capgroup.dcip.cmps.invoker.ApiClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.TimeZone;

/**
 * Application entry point for DCIP
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.capgroup.dcip", excludeFilters = {
        @Filter(type = FilterType.ASSIGNABLE_TYPE, value = ApiClient.class)})
public class Application {

    public static void main(String[] args) throws Throwable {
        SpringApplication.run(Application.class, args);
    }

    /**
     * Prints out all the beans at startup TODO: remove before going into production
     */
    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {

            String[] beanNames = ctx.getBeanDefinitionNames();
            Arrays.sort(beanNames);

            for (String beanName : beanNames) {
                System.out.println(beanName);
            }
        };
    }
}
