package ru.javaprojects.userservice.web.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.javaprojects.userservice.model.User;
import ru.javaprojects.userservice.service.UserService;
import ru.javaprojects.userservice.util.exception.NotFoundException;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class WebSecurity extends WebSecurityConfigurerAdapter {

    private final JwtAuthorizationFilter jwtAuthorizationFilter;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final RestAccessDeniedHandler restAccessDeniedHandler;
    private final UserService service;
    private final PasswordEncoder passwordEncoder;

    public WebSecurity(JwtAuthorizationFilter jwtAuthorizationFilter, RestAuthenticationEntryPoint restAuthenticationEntryPoint,
                       RestAccessDeniedHandler restAccessDeniedHandler, UserService service, PasswordEncoder passwordEncoder) {
        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
        this.restAccessDeniedHandler = restAccessDeniedHandler;
        this.service = service;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return email -> {
            try {
                User user = service.getByEmail(email.toLowerCase());
                return new AuthorizedUser(user);
            } catch (NotFoundException e) {
                throw new UsernameNotFoundException("User '" + email + "' was not found");
            }
        };
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder);
    }

    // TODO ADD CORS
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/api/profile/login").permitAll()
                .antMatchers("/api/profile/register").anonymous()
                .antMatchers("/api/profile/password/reset").anonymous()
                .antMatchers("/api/profile/email/verify").anonymous()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling().accessDeniedHandler(restAccessDeniedHandler)
                .authenticationEntryPoint(restAuthenticationEntryPoint);
    }
}