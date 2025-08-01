package com.example.cloudfour.peopleofdelivery.domain.user.repository;

import com.example.cloudfour.peopleofdelivery.domain.user.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserAddressRepository extends JpaRepository<UserAddress, UUID> {

    List<UserAddress> findAllByUser_Id(UUID userId);

    Optional<UserAddress> findByIdAndUser_Id(UUID addressId, UUID userId);

}
