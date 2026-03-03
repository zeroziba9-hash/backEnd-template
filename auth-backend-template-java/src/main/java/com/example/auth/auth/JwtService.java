package com.example.auth.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    @Value("${app.jwt.access-secret}")
    private String accessSecret;

    @Value("${app.jwt.refresh-secret}")
    private String refreshSecret;

    @Value("${app.jwt.access-exp-seconds:900}")
    private long accessExpSeconds;

    @Value("${app.jwt.refresh-exp-seconds:604800}")
    private long refreshExpSeconds;

    public String issueAccessToken(String userId, String email) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(userId)
                .claim("email", email)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(accessExpSeconds)))
                .signWith(key(accessSecret))
                .compact();
    }

    public String issueRefreshToken(String userId) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(userId)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(refreshExpSeconds)))
                .signWith(key(refreshSecret))
                .compact();
    }

    public Claims parseAccess(String token) {
        return Jwts.parser().verifyWith(key(accessSecret)).build().parseSignedClaims(token).getPayload();
    }

    public String parseRefreshSubject(String token) {
        return Jwts.parser().verifyWith(key(refreshSecret)).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public long getRefreshExpSeconds() {
        return refreshExpSeconds;
    }

    private SecretKey key(String raw) {
        return Keys.hmacShaKeyFor(raw.getBytes(StandardCharsets.UTF_8));
    }
}
