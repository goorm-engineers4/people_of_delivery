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
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    // Jwt Access, refresh 정보 가져오기
    private final JwtProperties jwtProperties;

    public TokenDto createToken(String email, Role role) {
        Date now = new Date();

        String accessToken = Jwts.builder()
                .setSubject(email)
                .claim("role", role.name())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + jwtProperties.getExpiration()))
                .signWith(SignatureAlgorithm.HS256, getSigningKey(jwtProperties.getSecret()))
                .compact();

        String refreshToken = Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + jwtProperties.getRefreshExpiration()))
                .signWith(SignatureAlgorithm.HS256, getSigningKey(jwtProperties.getRefreshSecret()))
                .compact();

        return new TokenDto("Bearer", accessToken, refreshToken, jwtProperties.getExpiration());
    }

    public String getEmailFromToken(String token, boolean isRefresh) {
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
}