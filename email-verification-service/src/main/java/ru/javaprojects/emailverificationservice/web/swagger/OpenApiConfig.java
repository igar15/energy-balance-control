package ru.javaprojects.emailverificationservice.web.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Energy Balance Control System. Email Verification Web Service Documentation",
                version = "1.0",
                description = "This page documents Email Verification Microservice RESTful Web Service endpoints<br><br>",
                license = @License(name = "Apache 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0"),
                contact = @Contact(url = "https://javaprojects.ru", name = "Igor Shlyakhtenkov", email = "ishlyakhtenkov@yandex.ru")
        ),
        servers = {@Server(url = "https://javaprojects.ru:8028/email-verification-service", description = "Internet Server url"),
                @Server(url = "http://localhost:8028/email-verification-service", description = "Local Server url")},
        tags = {@Tag(name = "Email Verification Rest Controller")}
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