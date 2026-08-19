package com.manna.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth ->
                    auth
                        .requestMatchers("/api/v1/oauth2/**").permitAll()
                        .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 ->
                    oauth2
                        .redirectionEndpoint(endpoint -> endpoint.baseUri("/api/v1/oauth2/callback/*"))
            );

        return http.build();
    }
}
