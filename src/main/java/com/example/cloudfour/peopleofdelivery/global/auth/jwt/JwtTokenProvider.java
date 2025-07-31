package com.example.cloudfour.peopleofdelivery.global.auth.jwt;

import com.example.cloudfour.peopleofdelivery.domain.user.enums.Role;
import com.example.cloudfour.peopleofdelivery.global.auth.config.JwtProperties;
import com.example.cloudfour.peopleofdelivery.global.auth.dto.TokenDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    /** Access: sub = userId, role 포함 / Refresh: sub = userId 만 */
    public TokenDto createToken(UUID userId, Role role) {
        Date now = new Date();
        Date accessExp  = new Date(now.getTime() + jwtProperties.getExpiration());
        Date refreshExp = new Date(now.getTime() + jwtProperties.getRefreshExpiration());

        String access = Jwts.builder()
                .setSubject(userId.toString()) // sub = UUID
                .claim("role", role.name())    // 최소 권한 정보
                .setIssuedAt(now)
                .setExpiration(accessExp)
                .signWith(getSigningKey(jwtProperties.getSecret()), SignatureAlgorithm.HS256)
                .compact();

        String refresh = Jwts.builder()
                .setSubject(userId.toString()) // 최소 정보만
                .setIssuedAt(now)
                .setExpiration(refreshExp)
                .signWith(getSigningKey(jwtProperties.getRefreshSecret()), SignatureAlgorithm.HS256)
                .compact();

        return new TokenDto("Bearer", access, refresh, jwtProperties.getExpiration());
    }

    public boolean isValidToken(String token, boolean isRefresh) {
        try {
            if (token == null || token.isBlank()) return false;
            getClaims(token, isRefresh);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public UUID getUserIdFromToken(String token, boolean isRefresh) {
        String sub = getClaims(token, isRefresh).getSubject();
        return UUID.fromString(sub);
    }

    public Role getRoleFromToken(String token) {
        String r = (String) getClaims(token, false).get("role");
        return Role.valueOf(r);
    }

    public Instant getExpirationInstant(String token, boolean isRefresh) {
        return getClaims(token, isRefresh).getExpiration().toInstant();
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
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); // 32B 이상
    }
}
