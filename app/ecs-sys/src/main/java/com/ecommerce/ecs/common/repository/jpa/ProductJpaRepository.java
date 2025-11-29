package com.ecommerce.ecs.common.repository.jpa;

import com.ecommerce.ecs.common.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductJpaRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByTenantId(Long tenantId);
    List<Product> findAllByCategoryIdAndTenantId(Long categoryId, Long tenantId);
    long countByTenantId(Long tenantId);
}

