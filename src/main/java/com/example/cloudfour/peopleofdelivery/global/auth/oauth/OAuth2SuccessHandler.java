package com.example.cloudfour.peopleofdelivery.global.auth.oauth;

import com.example.cloudfour.peopleofdelivery.global.auth.dto.TokenDTO;
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

        response.setContentType("application/json;charset=UTF-8");

        if (user == null) {
            var body = Map.of(
                    "needSignup", true,
                    "providerId", oAuth2User.getProviderId(),
                    "email", oAuth2User.getEmail()
            );

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(objectMapper.writeValueAsString(body));
            return;
        }

        TokenDTO tokenDto = jwtTokenProvider.createToken(user.getId(), user.getRole());

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(objectMapper.writeValueAsString(tokenDto));
    }
}
