package com.clinica.citas.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/** DTO de entrada para crear/actualizar un Paciente. */
public record PacienteRequestDTO(

        @NotBlank(message = "El RUT es obligatorio")
        @Size(max = 12, message = "El RUT no puede superar los 12 caracteres")
        String rut,

        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "El apellido es obligatorio")
        String apellido,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato valido")
        String email,

        @Size(max = 20, message = "El telefono no puede superar los 20 caracteres")
        String telefono,

        @Past(message = "La fecha de nacimiento debe ser anterior a hoy")
        LocalDate fechaNacimiento
) {
}
