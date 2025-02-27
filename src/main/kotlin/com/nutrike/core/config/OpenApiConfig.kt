package com.nutrike.core.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {
    @Bean
    fun configureOpenApi(): OpenAPI =
        OpenAPI()
            .info(
                Info()
                    .title("Nutrike API Documentation")
                    .version("1.0")
                    .description("API documentation for the Nutrike Backend Rest API")
                    .contact(Contact().name("Paul Brenker").email("paul.brenker@gmail.com")),
            ).addSecurityItem(SecurityRequirement().addList("BearerAuth"))
            .components(
                Components().addSecuritySchemes(
                    "BearerAuth",
                    SecurityScheme()
                        .name("BearerAuth")
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"),
                ),
            )
}
