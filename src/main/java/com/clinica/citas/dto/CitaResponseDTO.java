package com.clinica.citas.dto;

import java.time.LocalDate;
import java.time.LocalTime;

/** DTO de salida para Cita. */
public record CitaResponseDTO(
        Long id,
        LocalDate fecha,
        LocalTime hora,
        String motivo,
        String estado,
        Long medicoId,
        Long pacienteId,
        String pacienteNombre
) {
}
