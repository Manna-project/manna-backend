package com.manna.auth.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "spring.security.oauth2.client")
public record OAuth2ClientProperties(
        Map<String, ProviderConfig> provider,
        Map<String, RegistrationConfig> registration
) {
    public record ProviderConfig(
            String authorizationUri,
            String tokenUri,
            String userInfoUri,
            String userNameAttribute
    ) {}

    public record RegistrationConfig(
            String clientId,
            String clientSecret,
            String redirectUri,
            String authorizationGrantType,
            String clientAuthenticationMethod,
            String clientName,
            List<String> scope
    ) {}
}
