package com.manna.auth.config;

import com.manna.auth.handler.OAuth2LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final OAuth2LoginSuccessHandler successHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth ->
                    auth
                        .requestMatchers("/api/v1/oauth2/**").permitAll()
                        .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 ->
                    oauth2
                        .redirectionEndpoint(endpoint -> endpoint.baseUri("/api/v1/oauth2/callback/*"))
                        .successHandler(successHandler)
            );

        return http.build();
    }
}
