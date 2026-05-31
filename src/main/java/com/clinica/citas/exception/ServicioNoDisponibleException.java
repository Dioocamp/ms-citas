package com.clinica.citas.exception;

/**
 * Se lanza cuando un microservicio dependiente (ms-personal-medico)
 * no responde. El GlobalExceptionHandler la traduce a un HTTP 503.
 */
public class ServicioNoDisponibleException extends RuntimeException {

    public ServicioNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}
