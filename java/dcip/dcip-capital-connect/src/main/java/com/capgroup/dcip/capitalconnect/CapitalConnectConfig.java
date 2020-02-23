package com.capgroup.dcip.capitalconnect;

import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * Responsible for creating the RestTemplate for connectivity to Capital Connect
 * services
 */
@Configuration
@ToString
public class CapitalConnectConfig {
    @Value("${capital-connect.userName}")
    private String userName;

    @Value("${capital-connect.password}")
    private String password;

    @Value("${capital-connect.root-uri}")
    private String rootUri;

    @Bean
    public RestTemplate capitalConnectRestTemplate(RestTemplateBuilder builder) {
        builder = builder.rootUri(rootUri);
        builder = builder.requestFactory(HttpComponentsClientHttpRequestFactory.class);

        Authenticator.setDefault(new NtLmAuthenticator());

        return builder.build();
    }

    public class NtLmAuthenticator extends Authenticator {

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(userName, password.toCharArray());
        }
    }
}
