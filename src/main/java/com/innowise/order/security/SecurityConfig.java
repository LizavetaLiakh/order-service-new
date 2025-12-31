package com.innowise.order.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationConverter jwtAuthenticationConverter)
            throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/users/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/items/add").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/items/update/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/items/delete/**").hasRole("ADMIN")
                        .requestMatchers("/items/**").authenticated()
                        .requestMatchers("/orders/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/orders/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/orders/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/orders/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/orders/**").authenticated()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}