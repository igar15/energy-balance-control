package ru.javaprojects.userservice.web.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.servers.ServerVariable;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer"
)
@OpenAPIDefinition(
        info = @Info(
                title = "Energy Balance Control System. Users Web Service Documentation",
                version = "1.0",
                description = "This page documents Users Microservice RESTful Web Service endpoints<br><br>" +
                        "Use presented Authorization JWT token to authorize)",
                license = @License(name = "Apache 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0"),
                contact = @Contact(url = "https://javaprojects.ru", name = "Igor Shlyakhtenkov", email = "ishlyakhtenkov@yandex.ru")
        ),
        servers = {@Server(url = "https://javaprojects.ru/******", description = "Internet Server url"),
                @Server(url = "http://localhost:8028/user-service", description = "Local Server url", variables = @ServerVariable(name = "jwtToken", defaultValue = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJhdWQiOiJFbmVyZ3kgQmFsYW5jZSBDb250cm9sIFN5c3RlbSIsInN1YiI6IjEwMDAwMSIsImlzcyI6ImphdmFwcm9qZWN0cy5ydSIsImV4cCI6MTY0ODAyNzY4OSwiaWF0IjoxNjQ3NTk1Njg5LCJhdXRob3JpdGllcyI6WyJST0xFX0FETUlOIiwiUk9MRV9VU0VSIl19.LnCtskmZqzZE4jAaJxrZ5dkJ1ffMfP08SgSNblZR_28A_4BR79BQBQzS7NsHFxua25AGAddOKnRGONUihU7rzg"))},
        tags = {@Tag(name = "Profile Rest Controller"),
                @Tag(name = "Admin Rest Controller" + OpenApiConfig.ALLOWED_ADMIN)},
        security = @SecurityRequirement(name = "bearerAuth")
)
public class OpenApiConfig {
    public static final String ALLOWED_ADMIN = " (Allowed: ADMIN)";
    @Bean
    public GroupedOpenApi api() {
        return GroupedOpenApi.builder()
                .group("REST API")
                .pathsToMatch("/api/**")
                .build();
    }
}