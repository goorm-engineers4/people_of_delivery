package com.example.cloudfour.peopleofdelivery.domain.menu.repository;

import com.example.cloudfour.peopleofdelivery.domain.menu.entity.MenuCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface MenuOptionRepository extends JpaRepository<MenuCategory, UUID> {

}
