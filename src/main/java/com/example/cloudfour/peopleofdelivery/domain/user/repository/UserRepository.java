package com.example.cloudfour.peopleofdelivery.domain.user.repository;

import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findByProviderId(String providerId);
}