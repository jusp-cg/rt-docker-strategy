import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class DocumentTestConfig {
    @Value("${document.service.url}")
    private String documentStorageRootUri;

    @Bean
    public RestTemplate documentStorageRestTemplate(RestTemplateBuilder builder){
        builder = builder.rootUri(documentStorageRootUri);
        return builder.build();
    }
}
