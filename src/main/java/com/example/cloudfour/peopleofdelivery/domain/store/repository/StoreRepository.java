package com.example.cloudfour.peopleofdelivery.domain.store.repository;

import com.example.cloudfour.peopleofdelivery.domain.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface StoreRepository extends JpaRepository<Store, UUID> {

}

