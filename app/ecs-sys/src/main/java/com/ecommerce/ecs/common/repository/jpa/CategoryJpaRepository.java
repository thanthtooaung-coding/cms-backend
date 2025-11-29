package com.ecommerce.ecs.common.repository.jpa;

import com.ecommerce.ecs.common.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryJpaRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByTenantId(Long tenantId);
    long countByTenantId(Long tenantId);
}

