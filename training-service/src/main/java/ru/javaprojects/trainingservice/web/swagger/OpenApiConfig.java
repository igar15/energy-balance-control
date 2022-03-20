package ru.javaprojects.trainingservice.web.swagger;

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
                title = "Energy Balance Control System. Training Web Service Documentation",
                version = "1.0",
                description = "This page documents Training Microservice RESTful Web Service endpoints<br><br>" +
                        "For authorization, use the JWT token shown below",
                license = @License(name = "Apache 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0"),
                contact = @Contact(url = "https://javaprojects.ru", name = "Igor Shlyakhtenkov", email = "ishlyakhtenkov@yandex.ru")
        ),
        servers = {@Server(url = "https://javaprojects.ru:8028/training-service", description = "Internet Server url", variables = @ServerVariable(name = "jwtToken", defaultValue = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJhdWQiOiJFbmVyZ3kgQmFsYW5jZSBDb250cm9sIFN5c3RlbSIsInN1YiI6IjEwMDAwMSIsImlzcyI6ImphdmFwcm9qZWN0cy5ydSIsImV4cCI6MjUyNDU5NzIwMCwiaWF0IjoxNjQ3NzEwNzMwLCJhdXRob3JpdGllcyI6WyJST0xFX0FETUlOIl19.t1P7gby7QJHeZILxnXhy2fcC9YhnTJojgtrwbxE2i-6D_DCNxpeCkcxNpVYJdtv32QF4XkwmplgXUJXjudeMcQ")),
                @Server(url = "http://localhost:8028/training-service", description = "Local Server url", variables = @ServerVariable(name = "jwtToken", defaultValue = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJhdWQiOiJFbmVyZ3kgQmFsYW5jZSBDb250cm9sIFN5c3RlbSIsInN1YiI6IjEwMDAwMSIsImlzcyI6ImphdmFwcm9qZWN0cy5ydSIsImV4cCI6MjUyNDU5NzIwMCwiaWF0IjoxNjQ3NzA5NzQ1LCJhdXRob3JpdGllcyI6WyJST0xFX0FETUlOIl19.8EICsUjvRWMYnJ0DtErhIDQ0ctdTzQ2etfNMV2yZxgapQvAtvKIozEjOVWDWbm66R14n_-M5sJCUoLfFNO7r5A"))},
        tags = {@Tag(name = "Exercise Rest Controller"),
                @Tag(name = "Exercise Type Rest Controller")},
        security = @SecurityRequirement(name = "bearerAuth")
)
public class OpenApiConfig {
    @Bean
    public GroupedOpenApi api() {
        return GroupedOpenApi.builder()
                .group("REST API")
                .pathsToMatch("/api/**")
                .build();
    }
}