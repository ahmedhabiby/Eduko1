package com.springboot.eduko.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Configuration;

@Configuration

// ── API Info ───────────────────────────────────────────────────────────────
@OpenAPIDefinition(
    info = @Info(
        title       = "EDUKO Platform API",
        description = "REST API for the EDUKO online education platform.\n\n" +
                      "**Authentication:** All protected endpoints require a Bearer JWT token. " +
                      "Obtain a token from `POST /auth/login` or `POST /auth/register`, " +
                      "then click \"Authorize\" above and paste it.\n\n" +
                      "**Admin endpoints** are under `/admin/**` and require `ROLE_ADMIN`.\n\n" +
                      "**Environments:**\n" +
                      "- Local: `http://localhost:8080`\n" +
                      "- Production: set `VITE_API_BASE_URL` on frontend.",
        contact = @Contact(
            name  = "EDUKO Engineering",
            email = "ahmednsra329@gmail.com"
        ),
        license = @License(
            name = "Proprietary — All Rights Reserved"
        ),
        version = "v2.0"
    ),
    // ── Servers ───────────────────────────────────────────────
    servers = {
        @Server(url = "http://localhost:8080",  description = "Local Development"),
        @Server(url = "https://api.eduko.app", description = "Production")
    },
    // ── Global security (every endpoint requires JWT unless marked @SecurityRequirements({}))
    security = @SecurityRequirement(name = "BearerAuth"),
    // ── Tags order in Swagger UI ────────────────────────────────
    tags = {
        @Tag(name = "Authentication",       description = "Login, Register, Logout, Password Reset — public endpoints"),
        @Tag(name = "Student",              description = "Student profile and account management"),
        @Tag(name = "Courses",              description = "Browse and search published courses"),
        @Tag(name = "Enrollments",          description = "Course enrollment management"),
        @Tag(name = "Progress",             description = "Track lesson and course completion"),
        @Tag(name = "Favorites",            description = "Save and manage favorite courses"),
        @Tag(name = "Admin — Auth",         description = "Admin login and session management"),
        @Tag(name = "Admin — Users",        description = "Manage students, teachers, and platform users"),
        @Tag(name = "Admin — Courses",      description = "Full course, module, and lesson CRUD"),
        @Tag(name = "Admin — Enrollments",  description = "View and manage all enrollments"),
        @Tag(name = "Admin — Payments",     description = "Review payment proofs and manage billing"),
        @Tag(name = "Admin — Reports",      description = "Platform analytics and export"),
        @Tag(name = "Admin — Audit",        description = "Audit log of all admin actions")
    }
)
// ── JWT Bearer Security Scheme ───────────────────────────────────────────
@SecurityScheme(
    name        = "BearerAuth",
    type        = SecuritySchemeType.HTTP,
    scheme      = "bearer",
    bearerFormat = "JWT",
    in          = SecuritySchemeIn.HEADER,
    description = "Paste your JWT token here (without the \"Bearer \" prefix). " +
                  "Obtain it from POST /auth/login response field \"token\"."
)
public class ShowSwagger {
}
