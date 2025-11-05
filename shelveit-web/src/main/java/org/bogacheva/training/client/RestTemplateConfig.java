package org.bogacheva.training.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bogacheva.training.client.error.RestTemplateErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
    
    @Bean
    public RestTemplate restTemplate(RestTemplateErrorHandler errorHandler) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(errorHandler);
        return restTemplate;
    }
    
    @Bean
    public RestTemplateErrorHandler restTemplateErrorHandler(ObjectMapper objectMapper) {
        return new RestTemplateErrorHandler(objectMapper);
    }
}

