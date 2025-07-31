package com.example.cloudfour.peopleofdelivery.global.auth.repository;

import com.example.cloudfour.peopleofdelivery.domain.user.enums.VerificationPurpose;
import com.example.cloudfour.peopleofdelivery.global.auth.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, UUID> {
    Optional<VerificationCode> findByEmailAndCode(String email, String code);
    Optional<VerificationCode> findByEmailAndCodeAndPurpose(String email, String code, VerificationPurpose purpose);
    void deleteByEmailAndPurpose(String email, VerificationPurpose purpose);
    void deleteByEmail(String email);
}