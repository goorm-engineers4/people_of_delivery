package com.example.cloudfour.peopleofdelivery.global.auth.userdetails;

import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import com.example.cloudfour.peopleofdelivery.domain.user.enums.LoginType;
import com.example.cloudfour.peopleofdelivery.domain.user.enums.Role;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
public class CustomUserDetails implements UserDetails {

    private final UUID id;
    private final String email;
    private final String passwordHash;
    private final Role role;
    private final LoginType loginType;
    private final String providerId;

    public CustomUserDetails(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.passwordHash = user.getPassword();
        this.role = user.getRole();
        this.loginType = user.getLoginType();
        this.providerId = user.getProviderId();
    }

    public static CustomUserDetails of(UUID id, String email, Role role, LoginType loginType) {
        return new CustomUserDetails(id, email, null, role, loginType, null);
    }

    private CustomUserDetails(UUID id, String email, String passwordHash,
                              Role role, LoginType loginType, String providerId) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.loginType = loginType;
        this.providerId = providerId;
    }

    public UUID getId() { return id; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }


    @Override public boolean isAccountNonExpired()  { return true; }
    @Override public boolean isAccountNonLocked()   { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled()            { return true; }

    @Override
    public String toString() {
        return "CustomUserDetails{id=%s,role=%s"
                .formatted(id, role);
    }
}
