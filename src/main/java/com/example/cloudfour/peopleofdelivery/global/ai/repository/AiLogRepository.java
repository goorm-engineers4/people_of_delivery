package com.example.cloudfour.peopleofdelivery.global.ai.repository;

import com.example.cloudfour.peopleofdelivery.global.ai.entity.AiLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AiLogRepository extends JpaRepository<AiLog, UUID> {
}
