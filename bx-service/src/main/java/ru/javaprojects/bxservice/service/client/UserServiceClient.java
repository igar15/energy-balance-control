package ru.javaprojects.bxservice.service.client;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import ru.javaprojects.bxservice.to.UserDetails;

//TODO Make real feign client here
//@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping("/api/users/profile/details")
    UserDetails getUserDetails(long userId, @RequestHeader(HttpHeaders.AUTHORIZATION) String token);

    default UserDetails getUserDetails(long userId) {
        //Use JwtProvider to generate token for userId and pass it to feign method below
        return getUserDetails(userId, "");
    }
}