package com.springboot.eduko.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    private static final String BEARER_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("EDUKO LMS API")
                        .version("2.0.0")
                        .description("""
                                EDUKO Learning Management System — Backend API.

                                **Base URL (Student Platform):** `http://localhost:8080`

                                **Base URL (Admin Panel):** `http://localhost:8080/admin`
                                
                                **Authentication:** All protected endpoints require a Bearer JWT token.
                                Obtain the token via `POST /auth/login`.
                                """)
                        .contact(new Contact()
                                .name("EDUKO Dev Team")
                                .email("dev@eduko.com")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local Dev")
                ))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME))
                .components(new Components()
                        .addSecuritySchemes(BEARER_SCHEME,
                                new SecurityScheme()
                                        .name(BEARER_SCHEME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter your JWT token (without 'Bearer ' prefix)")));
    }

    /** Student Platform endpoints */
    @Bean
    public GroupedOpenApi studentApi() {
        return GroupedOpenApi.builder()
                .group("01-student-platform")
                .displayName("Student Platform")
                .pathsToMatch(
                        "/auth/**", "/signup", "/login",
                        "/courses/**", "/courses",
                        "/enrollments/**", "/enrollments",
                        "/progress/**", "/progress"
                )
                .pathsToExclude("/admin/**")
                .build();
    }

    /** Admin Panel endpoints */
    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("02-admin-panel")
                .displayName("Admin Panel")
                .pathsToMatch("/admin/**")
                .build();
    }
}
