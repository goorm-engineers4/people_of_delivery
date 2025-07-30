package com.example.cloudfour.peopleofdelivery.domain.user.entity;

import com.example.cloudfour.peopleofdelivery.domain.region.entity.Region;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "p_useraddress")
public class UserAddress {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "regionId", nullable = false)
    private Region region;

    public static class UserAddressBuilder {
        private UserAddressBuilder id(UUID id) {
            throw new UnsupportedOperationException("id 수동 생성 불가");
        }
    }

    public void setUser(User user) {
        this.user = user;
        user.getAddresses().add(this);
    }

    public void setRegion(Region region) {
        this.region = region;
        region.getAddresses().add(this);
    }
}
