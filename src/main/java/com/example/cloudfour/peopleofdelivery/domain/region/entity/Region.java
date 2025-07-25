package com.example.cloudfour.peopleofdelivery.domain.region.entity;

import com.example.cloudfour.peopleofdelivery.domain.useraddress.entity.UserAddress;
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
public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Column(nullable = false)
    private String si;

    @Column(nullable = false)
    private String gu;

    @Column(nullable = false)
    private String dong;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "region", cascade = CascadeType.ALL)
    @Builder.Default
    private List<UserAddress> addresses = new ArrayList<>();

    //TODO: 가게 OneToMany 추가
}
