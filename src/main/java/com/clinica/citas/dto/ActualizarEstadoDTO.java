package com.clinica.citas.dto;

import com.clinica.citas.model.EstadoCita;
import jakarta.validation.constraints.NotNull;

/** DTO para cambiar el estado de una cita (PATCH). */
public record ActualizarEstadoDTO(

        @NotNull(message = "El estado es obligatorio (PROGRAMADA, CONFIRMADA, ATENDIDA, CANCELADA)")
        EstadoCita estado
) {
}
