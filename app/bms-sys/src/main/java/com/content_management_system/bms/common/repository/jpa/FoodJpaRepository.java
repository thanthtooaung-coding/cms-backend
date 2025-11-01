package com.content_management_system.bms.common.repository.jpa;

import com.content_management_system.bms.common.enums.Category;
import com.content_management_system.bms.common.models.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodJpaRepository extends JpaRepository<Food, Long> {
    List<Food> findByCategory(Category category);
}
