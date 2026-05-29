package com.clinica.citas.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Metadatos de la documentacion OpenAPI (visible en /swagger-ui.html). */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI citasOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("API - Microservicio Citas")
                .description("Gestion de pacientes y citas medicas de la Clinica (EP2 - JVY0101)")
                .version("1.0.0")
                .contact(new Contact().name("Equipo Clinica")));
    }
}
