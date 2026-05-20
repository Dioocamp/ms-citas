package com.clinica.citas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del microservicio de Citas.
 * Gestiona pacientes y citas, y se comunica con ms-personal-medico
 * para validar y enriquecer los datos del medico asignado a una cita.
 */
@SpringBootApplication
public class CitasApplication {

    public static void main(String[] args) {
        SpringApplication.run(CitasApplication.class, args);
    }
}
