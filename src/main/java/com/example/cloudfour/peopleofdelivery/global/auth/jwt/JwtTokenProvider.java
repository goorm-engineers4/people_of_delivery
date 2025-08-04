package com.example.cloudfour.peopleofdelivery.global.auth.jwt;

import com.example.cloudfour.peopleofdelivery.global.auth.config.JwtProperties;
import com.example.cloudfour.peopleofdelivery.global.auth.dto.TokenDto;
import com.example.cloudfour.peopleofdelivery.domain.user.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    // Jwt Access, refresh 정보 가져오기
    private final JwtProperties jwtProperties;

    public TokenDto createToken(UUID userId, Role role) {
        Date now = new Date();
        String sub = userId.toString();

        String accessToken = Jwts.builder()
                .setSubject(sub)
                .claim("role", role.name())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + jwtProperties.getExpiration()))
                .signWith(getSigningKey(jwtProperties.getSecret()), SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .setSubject(sub)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + jwtProperties.getRefreshExpiration()))
                .signWith(getSigningKey(jwtProperties.getRefreshSecret()), SignatureAlgorithm.HS256)
                .compact();
        return new TokenDto("Bearer", accessToken, refreshToken, jwtProperties.getExpiration());
    }

    public String getIdFromToken(String token, boolean isRefresh) {
        return getClaims(token, isRefresh).getSubject();
    }

    public Role getRoleFromToken(String token) {
        String role = (String) getClaims(token, false).get("role");
        return Role.valueOf(role);
    }

    public boolean isValidToken(String token, boolean isRefresh) {
        try {
            getClaims(token, isRefresh);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims getClaims(String token, boolean isRefresh) {
        String key = isRefresh ? jwtProperties.getRefreshSecret() : jwtProperties.getSecret();
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey(key))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey(String secret) {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public Instant getExpirationInstant(String token, boolean isRefresh) {
        Claims claims = getClaims(token, isRefresh);
        return claims.getExpiration().toInstant();
    }
}