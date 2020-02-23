package com.capgroup.dcip.cmps;

import com.capgroup.dcip.cmps.invoker.ApiClient;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.text.SimpleDateFormat;

@Configuration
@ToString
public class CmpsConfig {

    @Value("${cmps.userName}")
    private String userName;

    @Value("${cmps.password}")
    private String password;

    @Value("${cmps.url}")
    private String url;

    @Bean
    public ApiClient apiClient(@Qualifier("cmpsRestTemplate") RestTemplate restTemplate) {

        ApiClient result = new ApiClient(restTemplate);
        result.setUsername(userName);
        result.setPassword(password);
        result.setBasePath(url);
        result.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));

        return result;
    }

    @Bean
    public RestTemplate cmpsRestTemplate(RestTemplateBuilder builder) {
        return builder.additionalInterceptors((ClientHttpRequestInterceptor) (request, body, execution) -> {
            request.getHeaders().set("client_id", userName);
            request.getHeaders().set("client_secret", password);
            return execution.execute(request, body);
        }).build();
    }
}
