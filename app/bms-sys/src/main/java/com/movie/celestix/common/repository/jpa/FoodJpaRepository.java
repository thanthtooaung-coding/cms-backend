package com.movie.celestix.common.repository.jpa;

import com.movie.celestix.common.enums.Category;
import com.movie.celestix.common.models.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodJpaRepository extends JpaRepository<Food, Long> {
    List<Food> findByCategory(Category category);
    List<Food> findAllByTenantId(Long tenantId);
    List<Food> findByCategoryAndTenantId(Category category, Long tenantId);
}
