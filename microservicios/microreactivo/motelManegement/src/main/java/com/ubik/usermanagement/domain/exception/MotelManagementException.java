package com.ubik.usermanagement.domain.exception;

/**
 * Excepción base para el dominio de gestión de moteles
 * Parte de la arquitectura hexagonal - Dominio
 */
public class MotelManagementException extends RuntimeException {

    public MotelManagementException(String message) {
        super(message);
    }

    public MotelManagementException(String message, Throwable cause) {
        super(message, cause);
    }
}
