package com.ubik.usermanagement.domain.exception;

/**
 * Excepción lanzada cuando un recurso solicitado no existe
 * Parte de la arquitectura hexagonal - Dominio
 */
public class ResourceNotFoundException extends MotelManagementException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceType, Long id) {
        super(String.format("%s no encontrado con ID: %d", resourceType, id));
    }
}
