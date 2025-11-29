package com.movie.celestix.common.repository.jpa;

import com.movie.celestix.common.models.FoodCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodCategoryJpaRepository extends JpaRepository<FoodCategory, Long> {
    List<FoodCategory> findAllByTenantId(Long tenantId);
}
