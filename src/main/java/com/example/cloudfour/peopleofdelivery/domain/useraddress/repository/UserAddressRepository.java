package com.example.cloudfour.peopleofdelivery.domain.useraddress.repository;

import com.example.cloudfour.peopleofdelivery.domain.useraddress.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface UserAddressRepository extends JpaRepository<UserAddress, UUID> {

}
