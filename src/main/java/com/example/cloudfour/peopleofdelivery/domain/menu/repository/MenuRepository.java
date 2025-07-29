package com.example.cloudfour.peopleofdelivery.domain.menu.repository;

import com.example.cloudfour.peopleofdelivery.domain.menu.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface MenuRepository extends JpaRepository<Menu, UUID> {
}
