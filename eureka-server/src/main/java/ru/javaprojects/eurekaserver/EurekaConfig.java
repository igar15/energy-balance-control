package ru.javaprojects.eurekaserver;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableEurekaServer
@ConditionalOnProperty(name = "eureka.enabled")
public class EurekaConfig {
}