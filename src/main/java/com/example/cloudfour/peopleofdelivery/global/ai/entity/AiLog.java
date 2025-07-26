package com.example.cloudfour.peopleofdelivery.global.ai.entity;

import com.example.cloudfour.peopleofdelivery.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "p_ailog")
public class AiLog extends BaseEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @Lob
    @Column(nullable = false)
    private String question;

    @Lob
    @Column(nullable = false)
    private String result;

    @Lob
    private String options;

    public static class AiLogBuilder {
        private AiLogBuilder id(UUID id) {
            throw new UnsupportedOperationException("id 생성 불가");
        }
    }
}