package ru.javaprojects.bxservice.service.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ru.javaprojects.bxservice.util.exception.UserServiceConnectionException;
import ru.javaprojects.energybalancecontrolshared.web.security.JwtProvider;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
public class UserServiceClient {
    private final JwtProvider jwtProvider;
    private final Environment environment;
    private final UserServiceFeignClient userServiceFeignClient;

    public UserServiceClient(JwtProvider jwtProvider, Environment environment, UserServiceFeignClient userServiceFeignClient) {
        this.jwtProvider = jwtProvider;
        this.environment = environment;
        this.userServiceFeignClient = userServiceFeignClient;
    }

    public UserBxDetails getUserBxDetails(long userId) {
        String authorizationHeader = getAuthorizationHeader(userId);
        return userServiceFeignClient.getUserBxDetails(authorizationHeader);
    }

    private String getAuthorizationHeader(long userId) {
        String authorizationHeader;
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return generateAuthorizationHeader(userId);
        }
        HttpServletRequest request = requestAttributes.getRequest();
        if (request == null) {
            return generateAuthorizationHeader(userId);
        }
        authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader == null) {
            return generateAuthorizationHeader(userId);
        }
        return authorizationHeader;
    }

    private String generateAuthorizationHeader(long userId) {
        return environment.getProperty("authorization.token.header.prefix") + jwtProvider.generateAuthorizationToken(String.valueOf(userId));
    }

    @FeignClient(name = "gateway-server")
    public interface UserServiceFeignClient {
        Logger logger = LoggerFactory.getLogger(UserServiceFeignClient.class);

        @GetMapping("/user-service/api/profile/bx-details")
        @CircuitBreaker(name = "user-service-get-bx-details", fallbackMethod = "throwFailedConnectionException")
        UserBxDetails getUserBxDetails(@RequestHeader(AUTHORIZATION) String authorizationHeader);

        default UserBxDetails throwFailedConnectionException(Exception exception) {
            logger.error("failed to get user bx details from user-service:" + exception.getLocalizedMessage());
            throw new UserServiceConnectionException("Failed to connect to user-service. Try again later");
        }
    }
}