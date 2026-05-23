package com.clinica.citas.dto;

import java.time.LocalDate;

/** DTO de salida para Paciente. */
public record PacienteResponseDTO(
        Long id,
        String rut,
        String nombre,
        String apellido,
        String email,
        String telefono,
        LocalDate fechaNacimiento,
        int cantidadCitas
) {
}
