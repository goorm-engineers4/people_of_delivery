package com.example.cloudfour.peopleofdelivery.domain.review.entity;

import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import com.example.cloudfour.peopleofdelivery.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Getter
@SuperBuilder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_review")
public class Review extends BaseEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private Integer score;

    @Column(nullable = false)
    private String content;

    @Lob
    private String pictureUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;
}
