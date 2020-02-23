package com.capgroup.dcip.document_storage;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Configuration
public class DocumentStorageConfig {
    @Value("${document.service.url}")
    private String documentStorageRootUri;
    private TimeBasedGenerator uuidGenerator = Generators.timeBasedGenerator();

    @Bean
    public RestTemplate documentStorageRestTemplate(RestTemplateBuilder builder){
        builder = builder.rootUri(documentStorageRootUri).interceptors(new DocumentStorageHttpRequestInterceptor());
        return builder.build();
    }

    private class DocumentStorageHttpRequestInterceptor implements ClientHttpRequestInterceptor {
        private final Log log = LogFactory.getLog(ClientHttpRequestInterceptor.class);

        @Override
        public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution execution) throws IOException {
            log.info(httpRequest);
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .currentRequestAttributes();

            // try getting the value from the request
            HttpServletRequest request = attributes.getRequest();
            httpRequest.getHeaders().add("DocumentServiceId", uuidGenerator.generate().toString());
            httpRequest.getHeaders().add("CorrelationId", request.getHeader("CorrelationId"));
            ClientHttpResponse response = execution.execute(httpRequest, bytes);
            return response;
        }
    }
}
