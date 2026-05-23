package com.clinica.citas.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

/** DTO de entrada para agendar/actualizar una Cita. */
public record CitaRequestDTO(

        @NotNull(message = "La fecha es obligatoria")
        @FutureOrPresent(message = "La fecha de la cita no puede ser pasada")
        LocalDate fecha,

        @NotNull(message = "La hora es obligatoria")
        LocalTime hora,

        @NotBlank(message = "El motivo es obligatorio")
        String motivo,

        @NotNull(message = "Debe indicar el id del medico")
        Long medicoId,

        @NotNull(message = "Debe indicar el id del paciente")
        Long pacienteId
) {
}
