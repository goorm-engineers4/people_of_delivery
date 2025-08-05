package com.example.cloudfour.peopleofdelivery.domain.user.entity;

import com.example.cloudfour.peopleofdelivery.domain.region.entity.Region;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;

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
        if (this.region != null) {
            this.region.getAddresses().remove(this);
        }
        this.region = region;
        region.getAddresses().add(this);
    }

    public void changeAddress(String address) {
        this.address = address;
    }
}
