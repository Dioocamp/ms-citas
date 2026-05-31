package com.clinica.citas.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Representacion local (parcial) del Medico que expone ms-personal-medico.
 * @JsonIgnoreProperties evita errores si el otro servicio agrega campos nuevos.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MedicoDTO(
        Long id,
        String nombre,
        String apellido,
        String email,
        Long especialidadId,
        String especialidadNombre
) {
}
