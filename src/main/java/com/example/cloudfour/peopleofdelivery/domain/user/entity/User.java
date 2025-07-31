package com.example.cloudfour.peopleofdelivery.domain.user.entity;

import com.example.cloudfour.peopleofdelivery.domain.cart.entity.Cart;
import com.example.cloudfour.peopleofdelivery.domain.order.entity.Order;
import com.example.cloudfour.peopleofdelivery.domain.review.entity.Review;
import com.example.cloudfour.peopleofdelivery.domain.store.entity.Store;
import com.example.cloudfour.peopleofdelivery.domain.user.enums.LoginType;
import com.example.cloudfour.peopleofdelivery.domain.user.enums.Role;
import com.example.cloudfour.peopleofdelivery.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "p_user")
public class User extends BaseEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email; // 확정 이메일

    @Column(unique = false)
    private String pendingEmail; // 검증 대기 중인 새 이메일

    @Column(nullable = false)
    private String nickname;

    private String password; // 자체 로그인(LOCAL)만 사용

    @Column(nullable = false)
    private String number;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoginType loginType;

    private String providerId; // 소셜 로그인 고유 식별자 (sub ..)

    @Column(nullable = false)
    @Builder.Default
    private boolean deleted = false; // 소프트 삭제 여부

    @Column(nullable = false)
    @Builder.Default
    private boolean emailVerified = false; // 현재 email의 인증 여부

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
    @Builder.Default
    private List<UserAddress> addresses = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
    private Cart cart;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Order> orders = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Store> stores = new ArrayList<>();

    public static class UserBuilder {
        private UserBuilder id(UUID id) {
            throw new UnsupportedOperationException("id 수동 생성 불가");
        }
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public void changePassword(String encodedPassword) { this.password = encodedPassword; }
    public void changeNickname(String nickname) { this.nickname = nickname; }
    public void changeNumber(String number) { this.number = number; }

    // 새 이메일로 변경 요청 — 실제 email 교체는 아님
    public void requestEmailChange(String newEmail) { this.pendingEmail = newEmail; }

    // 새 이메일 인증 성공 시에만 확정
    public void confirmEmailChange() {
        if (this.pendingEmail == null) return;
        this.email = this.pendingEmail;
        this.pendingEmail = null;
        this.emailVerified = true; // 새 이메일을 인증
    }

    public void markEmailVerified() { this.emailVerified = true; }
    // 소프트 삭제
    public void softDelete() { this.deleted = true; }
}