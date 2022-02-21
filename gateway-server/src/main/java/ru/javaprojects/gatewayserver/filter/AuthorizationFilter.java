package ru.javaprojects.gatewayserver.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class AuthorizationFilter implements GlobalFilter {
    private Environment environment;

    @Value("${gateway-server.free-paths}")
    private List<String> freePaths;

    public AuthorizationFilter(Environment environment) {
        this.environment = environment;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        if (!isFreePath(request.getPath().toString())) {
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }
            String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            if (!authorizationHeader.startsWith(this.environment.getProperty("authorization.token.header.prefix"))) {
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }
        }
        return chain.filter(exchange);
    }

    private boolean isFreePath(String path) {
        return freePaths.contains(path);
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        return response.setComplete();
    }
}