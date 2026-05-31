package com.clinica.citas.dto;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO enriquecido de una cita: combina datos propios (paciente) con datos
 * del medico obtenidos en tiempo real desde ms-personal-medico.
 * Demuestra la comunicacion entre microservicios.
 */
public record CitaDetalleDTO(
        Long id,
        LocalDate fecha,
        LocalTime hora,
        String motivo,
        String estado,
        Long pacienteId,
        String pacienteNombre,
        Long medicoId,
        String medicoNombre,
        String medicoEspecialidad
) {
}
