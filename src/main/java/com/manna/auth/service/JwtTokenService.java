package com.manna.auth.service;

import com.manna.auth.entity.User;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;

@Service
public class JwtTokenService {
    private final SecretKey secretKey;

    public JwtTokenService(@Value("${JWT_SECRET}") String secretKey) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    private String createToken(User user, String tokenType, Duration expiration) {
        return Jwts.builder()
            .subject(user.getEntityId().toString())
            .claim("type", tokenType)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expiration.toMillis()))
            .signWith(secretKey)
            .compact();
    }

    public String createAccessToken(User user) {
        return createToken(
            user,
            "access",
            Duration.ofMinutes(15)
        );
    }

    public String createRefreshToken(User user) {
        return createToken(
            user,
            "refresh",
            Duration.ofDays(14)
        );
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);

            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
