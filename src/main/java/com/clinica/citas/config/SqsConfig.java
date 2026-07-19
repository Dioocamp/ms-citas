package com.clinica.citas.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

/**
 * Cliente de AWS SQS, creado solo si las notificaciones estan habilitadas
 * (clinica.notificaciones.enabled=true / variable NOTIFICACIONES_ENABLED).
 *
 * Las credenciales se resuelven con la cadena por defecto del SDK:
 * variables de entorno (AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY,
 * AWS_SESSION_TOKEN), archivo de credenciales compartido o rol IAM.
 * Nunca se escriben credenciales en el codigo ni en el repositorio.
 */
@Configuration
@ConditionalOnProperty(name = "clinica.notificaciones.enabled", havingValue = "true")
public class SqsConfig {

    @Bean
    public SqsClient sqsClient(@Value("${AWS_REGION:us-east-1}") String region) {
        return SqsClient.builder()
                .region(Region.of(region))
                .httpClient(UrlConnectionHttpClient.create())
                .build();
    }
}
