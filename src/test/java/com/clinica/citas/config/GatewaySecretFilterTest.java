package com.clinica.citas.config;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/** Pruebas unitarias del filtro de proteccion del API Gateway. */
class GatewaySecretFilterTest {

    private static MockHttpServletRequest peticionApi() {
        return new MockHttpServletRequest("GET", "/api/citas");
    }

    @Test
    void sinSecretoConfigurado_dejaPasarLasPeticiones() throws Exception {
        GatewaySecretFilter filtro = new GatewaySecretFilter("");
        MockFilterChain cadena = new MockFilterChain();

        filtro.doFilter(peticionApi(), new MockHttpServletResponse(), cadena);

        assertNotNull(cadena.getRequest());
    }

    @Test
    void conSecretoYCabeceraCorrecta_dejaPasar() throws Exception {
        GatewaySecretFilter filtro = new GatewaySecretFilter("secreto-gw");
        MockHttpServletRequest peticion = peticionApi();
        peticion.addHeader(GatewaySecretFilter.HEADER, "secreto-gw");
        MockFilterChain cadena = new MockFilterChain();

        filtro.doFilter(peticion, new MockHttpServletResponse(), cadena);

        assertNotNull(cadena.getRequest());
    }

    @Test
    void conSecretoYSinCabecera_devuelve401() throws Exception {
        GatewaySecretFilter filtro = new GatewaySecretFilter("secreto-gw");
        MockHttpServletResponse respuesta = new MockHttpServletResponse();
        MockFilterChain cadena = new MockFilterChain();

        filtro.doFilter(peticionApi(), respuesta, cadena);

        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, respuesta.getStatus());
        assertNull(cadena.getRequest());
    }

    @Test
    void conSecreto_rutasNoApiComoActuator_noSeFiltran() throws Exception {
        GatewaySecretFilter filtro = new GatewaySecretFilter("secreto-gw");
        MockHttpServletRequest peticion =
                new MockHttpServletRequest("GET", "/actuator/health");
        MockFilterChain cadena = new MockFilterChain();

        filtro.doFilter(peticion, new MockHttpServletResponse(), cadena);

        assertNotNull(cadena.getRequest());
    }
}
