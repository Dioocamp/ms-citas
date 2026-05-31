package com.clinica.citas.exception;

import java.time.LocalDateTime;
import java.util.Map;

/** Estructura uniforme de respuesta de error. */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> fieldErrors
) {
    public ErrorResponse(int status, String error, String message, String path) {
        this(LocalDateTime.now(), status, error, message, path, null);
    }
}
