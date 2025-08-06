package com.example.cloudfour.peopleofdelivery.global.auth.repository;

import com.example.cloudfour.peopleofdelivery.global.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
}
