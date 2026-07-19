package com.clinica.citas.config;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.headerDoesNotExist;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Verifica que las llamadas internas hacia ms-personal-medico incluyan la
 * cabecera 'x-gateway-secret'.
 *
 * Sin ella, el filtro de seguridad del otro microservicio responde 401 y la
 * creacion de citas falla en produccion (aunque todo compile y el resto de
 * las pruebas pase).
 */
class RestClientConfigTest {

    private static final String BASE_URL = "http://ms-personal-medico:8081";

    @Test
    void conSecretoConfigurado_lasLlamadasInternasLlevanLaCabecera() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer servidor = MockRestServiceServer.bindTo(builder).build();

        RestClient cliente = new RestClientConfig()
                .medicoRestClient(builder, BASE_URL, "secreto-de-prueba");

        servidor.expect(requestTo(BASE_URL + "/api/medicos/1"))
                .andExpect(header(GatewaySecretFilter.HEADER, "secreto-de-prueba"))
                .andRespond(withSuccess("{\"id\":1}", MediaType.APPLICATION_JSON));

        cliente.get().uri("/api/medicos/{id}", 1).retrieve().body(String.class);

        servidor.verify();
    }

    @Test
    void sinSecretoConfigurado_noSeAgregaLaCabecera() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer servidor = MockRestServiceServer.bindTo(builder).build();

        RestClient cliente = new RestClientConfig()
                .medicoRestClient(builder, BASE_URL, "");

        servidor.expect(requestTo(BASE_URL + "/api/medicos/1"))
                .andExpect(headerDoesNotExist(GatewaySecretFilter.HEADER))
                .andRespond(withSuccess("{\"id\":1}", MediaType.APPLICATION_JSON));

        cliente.get().uri("/api/medicos/{id}", 1).retrieve().body(String.class);

        servidor.verify();
    }
}
