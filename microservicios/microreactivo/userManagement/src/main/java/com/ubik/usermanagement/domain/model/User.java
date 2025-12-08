package com.ubik.usermanagement.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * User domain model.
 * 
 * ARCHITECTURAL NOTES:
 * - This model currently mixes domain concerns with infrastructure (R2DBC annotations)
 * - In pure hexagonal architecture, domain models should be free of framework dependencies
 * - Consider creating separate persistence entities if this becomes a concern
 * - Password should be hashed before storage (currently plain text)
 * - resetToken and resetTokenExpiry might belong to a separate PasswordReset aggregate
 * 
 * TODO: This microservice is incomplete - missing:
 * - Repository interface
 * - Service layer
 * - Web handlers/controllers
 * - Security configuration
 */
@Table("users")
public record User(
        @Id Long id,
        String username,
        String password,
        String email,
        String role,
        String resetToken,
        java.time.LocalDateTime resetTokenExpiry
) {
}

