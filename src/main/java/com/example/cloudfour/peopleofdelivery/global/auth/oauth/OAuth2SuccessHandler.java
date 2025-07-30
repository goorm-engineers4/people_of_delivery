package com.example.cloudfour.peopleofdelivery.global.auth.oauth;

import com.example.cloudfour.peopleofdelivery.global.auth.dto.TokenDto;
import com.example.cloudfour.peopleofdelivery.global.auth.jwt.JwtTokenProvider;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        User user = oAuth2User.getUser();

        if (user == null) {
            // 신규 유저 → 추가 정보 입력 요청
            Map<String, String> result = new HashMap<>();
            result.put("providerId", oAuth2User.getProviderId());
            result.put("email", oAuth2User.getEmail());

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(result));
            return;
        }

        // 기존 유저 → JWT 발급
        TokenDto tokenDto = jwtTokenProvider.createToken(user.getEmail(), user.getRole());

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(tokenDto));
    }
}
