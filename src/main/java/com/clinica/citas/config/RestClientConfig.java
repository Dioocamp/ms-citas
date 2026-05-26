package com.clinica.citas.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Configura el RestClient usado para comunicarse con ms-personal-medico.
 * La URL base se lee de la propiedad 'ms.personal-medico.url'.
 */
@Configuration
public class RestClientConfig {

    @Bean
    public RestClient medicoRestClient(RestClient.Builder builder,
                                       @Value("${ms.personal-medico.url}") String baseUrl) {
        return builder.baseUrl(baseUrl).build();
    }
}
