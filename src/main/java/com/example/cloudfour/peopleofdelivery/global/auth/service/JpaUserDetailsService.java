package com.example.cloudfour.peopleofdelivery.global.auth.service;

import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import com.example.cloudfour.peopleofdelivery.domain.user.enums.LoginType;
import com.example.cloudfour.peopleofdelivery.domain.user.repository.UserRepository;
import com.example.cloudfour.peopleofdelivery.global.auth.userdetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JpaUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new UsernameNotFoundException("No user: " + email));

        // 비밀번호 기반 인증은 LOCAL만 허용
        if (user.getLoginType() != LoginType.LOCAL) {
            throw new DisabledException("Password login not allowed for " + user.getLoginType());
        }
        return new CustomUserDetails(user);
    }
}
