package com.clinica.citas.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro que protege los endpoints de negocio (/api/**) cuando el sistema
 * se expone a traves de un API Gateway.
 *
 * Si la propiedad 'clinica.gateway.secret' esta definida (variable de
 * entorno GATEWAY_SECRET), toda peticion a /api/** debe incluir la cabecera
 * 'x-gateway-secret' con ese valor. El API Gateway inyecta la cabecera en la
 * integracion, de modo que el acceso directo al contenedor queda bloqueado
 * y el Gateway pasa a ser la unica puerta de entrada al backend.
 *
 * Sin la propiedad definida (desarrollo local) el filtro no interviene.
 */
@Component
public class GatewaySecretFilter extends OncePerRequestFilter {

    public static final String HEADER = "x-gateway-secret";

    private final String secreto;

    public GatewaySecretFilter(@Value("${clinica.gateway.secret:}") String secreto) {
        this.secreto = secreto;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return secreto.isBlank() || !request.getRequestURI().startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (secreto.equals(request.getHeader(HEADER))) {
            filterChain.doFilter(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                    "Acceso directo no autorizado: utilice el API Gateway.");
        }
    }
}
