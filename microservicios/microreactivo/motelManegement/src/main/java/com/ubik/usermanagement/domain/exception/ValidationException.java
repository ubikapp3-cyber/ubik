package com.ubik.usermanagement.domain.exception;

/**
 * Excepción lanzada cuando falla la validación de negocio
 * Parte de la arquitectura hexagonal - Dominio
 */
public class ValidationException extends MotelManagementException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
