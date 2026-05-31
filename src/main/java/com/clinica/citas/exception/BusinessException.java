package com.clinica.citas.exception;

/** Violacion de una regla de negocio -> HTTP 409. */
public class BusinessException extends RuntimeException {

    public BusinessException(String mensaje) {
        super(mensaje);
    }
}
