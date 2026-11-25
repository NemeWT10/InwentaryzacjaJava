package pl.pollub.inwentaryzacja.server.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Dokumentacja REST API: http://localhost:8080/swagger-ui.html (opis JSON: /v3/api-docs).
 * Przycisk „Authorize” przyjmuje token z POST /api/auth/login.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(title = "System inwentaryzacji płytek ewaluacyjnych – REST API", version = "1.0",
                description = "Ewidencja sprzętu pracowni systemów wbudowanych z podziałem na role "
                        + "STUDENT, LECTURER i ADMIN. Brak lub zły token: 401, zła rola: 403."),
        security = @SecurityRequirement(name = OpenApiConfig.BEARER))
@SecurityScheme(name = OpenApiConfig.BEARER, type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class OpenApiConfig {

    static final String BEARER = "bearerAuth";
}
