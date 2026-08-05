package com.staysmart.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Swagger / OpenAPI documentation, browsable at {@code /api/swagger-ui.html}. */
@Configuration
public class OpenApiConfig {

    private static final String BEARER_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI staySmartOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("StaySmart AI API")
                        .description("REST API for the StaySmart AI vacation rental platform: "
                                + "authentication, property management, bookings, reviews, admin analytics "
                                + "and AI-powered features (recommendations, chat, trip/budget planning, "
                                + "smart search, review summarization, description generation).")
                        .version("v1.0.0")
                        .contact(new Contact().name("StaySmart AI Team").email("support@staysmart.ai")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME))
                .components(new Components().addSecuritySchemes(BEARER_SCHEME,
                        new SecurityScheme()
                                .name(BEARER_SCHEME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
