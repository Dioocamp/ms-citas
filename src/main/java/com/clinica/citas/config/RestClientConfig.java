package com.clinica.citas.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Configura el RestClient usado para comunicarse con ms-personal-medico.
 * La URL base se lee de la propiedad 'ms.personal-medico.url'.
 *
 * Cuando el sistema esta protegido por el API Gateway, ms-personal-medico
 * exige la cabecera 'x-gateway-secret' en todas sus rutas /api/**. Esta
 * llamada es interna (de un microservicio al otro, por la red overlay del
 * cluster), asi que debe incluir esa misma cabecera: de lo contrario el
 * filtro del otro servicio la rechazaria con 401.
 */
@Configuration
public class RestClientConfig {

    @Bean
    public RestClient medicoRestClient(RestClient.Builder builder,
                                       @Value("${ms.personal-medico.url}") String baseUrl,
                                       @Value("${clinica.gateway.secret:}") String gatewaySecret) {
        RestClient.Builder configurado = builder.baseUrl(baseUrl);

        if (!gatewaySecret.isBlank()) {
            configurado = configurado.defaultHeader(GatewaySecretFilter.HEADER, gatewaySecret);
        }

        return configurado.build();
    }
}
