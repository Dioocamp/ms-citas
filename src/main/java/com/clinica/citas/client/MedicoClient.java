package com.clinica.citas.client;

import com.clinica.citas.exception.ServicioNoDisponibleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.util.Optional;

/**
 * Cliente HTTP que consume el microservicio ms-personal-medico.
 * Encapsula la comunicacion entre microservicios usando RestClient (Spring 6.1+).
 */
@Component
public class MedicoClient {

    private static final Logger log = LoggerFactory.getLogger(MedicoClient.class);

    private final RestClient restClient;

    public MedicoClient(RestClient medicoRestClient) {
        this.restClient = medicoRestClient;
    }

    /**
     * Busca un medico por su id en ms-personal-medico.
     *
     * @return Optional con el medico, o vacio si el medico no existe (404).
     * @throws ServicioNoDisponibleException si ms-personal-medico no responde.
     */
    public Optional<MedicoDTO> buscarMedico(Long medicoId) {
        try {
            MedicoDTO medico = restClient.get()
                    .uri("/api/medicos/{id}", medicoId)
                    .retrieve()
                    .body(MedicoDTO.class);
            return Optional.ofNullable(medico);
        } catch (HttpClientErrorException.NotFound ex) {
            // El servicio respondio, pero el medico no existe.
            return Optional.empty();
        } catch (ResourceAccessException ex) {
            // No se pudo establecer conexion con el microservicio.
            log.warn("ms-personal-medico no esta disponible: {}", ex.getMessage());
            throw new ServicioNoDisponibleException(
                    "El microservicio de personal medico no esta disponible en este momento.");
        }
    }
}
