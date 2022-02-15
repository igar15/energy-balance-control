package ru.javaprojects.bxservice.service.client;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import ru.javaprojects.bxservice.to.UserBxDetails;

//TODO Make real feign client here
//@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping("/api/users/profile/bx-details")
    UserBxDetails getUserBxDetails(long userId, @RequestHeader(HttpHeaders.AUTHORIZATION) String token);

    default UserBxDetails getUserBxDetails(long userId) {
        //Use JwtProvider to generate token for userId and pass it to feign method below
        return getUserBxDetails(userId, "");
    }
}