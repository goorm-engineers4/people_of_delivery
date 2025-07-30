package com.example.cloudfour.peopleofdelivery.global.auth.oauth;

import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

    private final User user;
    private final Map<String, Object> attributes;
    private final String providerId;
    private final String email;

    public CustomOAuth2User(User user, Map<String, Object> attributes, String providerId, String email) {
        this.user = user;
        this.attributes = attributes;
        this.providerId = providerId;
        this.email = email;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getName() {
        return email;
    }

    public User getUser() {
        return user;
    }

    public String getProviderId() {
        return providerId;
    }

    public String getEmail() {
        return email;
    }
}
