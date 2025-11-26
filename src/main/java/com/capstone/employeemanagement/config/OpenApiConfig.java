package com.capstone.employeemanagement.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for OpenAPI/Swagger documentation.  Defines a security scheme
 * named "bearerAuth" so that Swagger UI can authenticate requests using a
 * JWT.  The {@link OpenAPIDefinition} annotation adds metadata to the
 * generated documentation such as the title and version.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(title = "Employee Management API", version = "1.0", description = "RESTful API for managing employees with JWT authentication"),
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {
    // No implementation needed.  Annotations drive the configuration.
}