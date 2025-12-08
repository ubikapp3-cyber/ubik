package com.ubik.usermanagement.domain.exception;

/**
 * Excepción lanzada cuando se viola una regla de negocio
 * Parte de la arquitectura hexagonal - Dominio
 */
public class BusinessRuleException extends MotelManagementException {

    public BusinessRuleException(String message) {
        super(message);
    }

    public BusinessRuleException(String message, Throwable cause) {
        super(message, cause);
    }
}
