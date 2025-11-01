package com.content_management_system.bms.common.repository.jpa;

import com.content_management_system.bms.common.models.FoodCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodCategoryJpaRepository extends JpaRepository<FoodCategory, Long> {
}
