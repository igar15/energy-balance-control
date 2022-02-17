package ru.javaprojects.userservice.web.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.javaprojects.userservice.util.exception.ErrorInfo;
import ru.javaprojects.userservice.web.json.JacksonObjectMapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static ru.javaprojects.userservice.util.exception.ErrorType.BAD_TOKEN_ERROR;
import static ru.javaprojects.userservice.web.AppExceptionHandler.EXCEPTION_BAD_TOKEN;


@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final Environment environment;
    private final JwtProvider jwtProvider;
    private final ObjectMapper mapper = JacksonObjectMapper.getMapper();

    public JwtAuthorizationFilter(Environment environment, JwtProvider jwtProvider) {
        this.environment = environment;
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getMethod().equalsIgnoreCase(HttpMethod.OPTIONS.name())) {
            response.setStatus(HttpStatus.OK.value());
        } else {
            String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authorizationHeader == null || !authorizationHeader.startsWith(environment.getProperty("authorization.token.header.prefix"))) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = authorizationHeader.replace(environment.getProperty("authorization.token.header.prefix"), "");
            try {
                String userId = jwtProvider.getSubject(token);
                if (jwtProvider.isTokenValid(userId, token) && SecurityContextHolder.getContext().getAuthentication() == null) {
                    List<GrantedAuthority> authorities = jwtProvider.getAuthorities(token);
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(userId, null, authorities);
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                } else {
                    SecurityContextHolder.clearContext();
                }
            } catch (JWTVerificationException e) {
                sendBadTokenResponse(request, response);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private void sendBadTokenResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ErrorInfo responseEntity = new ErrorInfo(request.getRequestURL(), BAD_TOKEN_ERROR,
                BAD_TOKEN_ERROR.getErrorCode(), EXCEPTION_BAD_TOKEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        ServletOutputStream outputStream = response.getOutputStream();
        mapper.writeValue(outputStream, responseEntity);
        outputStream.flush();
    }
}