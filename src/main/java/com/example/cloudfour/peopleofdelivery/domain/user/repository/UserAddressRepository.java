package com.example.cloudfour.peopleofdelivery.domain.user.repository;

import com.example.cloudfour.peopleofdelivery.domain.user.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserAddressRepository extends JpaRepository<UserAddress, UUID> {

    List<UserAddress> findAllByUser_Id(UUID userId);

    Optional<UserAddress> findByIdAndUser_Id(UUID addressId, UUID userId);

    @Query("select count(ua) > 0 from UserAddress ua where ua.user.isDeleted = false and ua.user.id =:userId and ua.id =:addressId")
    boolean existsByUserIdAndAddressId(@Param("userId") UUID userId, @Param("addressId") UUID addressId);
}
