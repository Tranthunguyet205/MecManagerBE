package com.example.mecManager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * Swagger/OpenAPI Configuration
 * Provides documentation for the Medical Manager API
 */
@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Medical Manager (MecManager) API")
            .version("1.0.0")
            .description("REST API for Medical Manager - Prescription Management System for Healthcare Facilities"))
        .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"))
        .components(new io.swagger.v3.oas.models.Components()
            .addSecuritySchemes("bearer-jwt",
                new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("JWT Bearer Token for authentication")));
  }
}
