package com.ecommerce.ecs.common.repository.jpa;

import com.ecommerce.ecs.common.models.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewJpaRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByProductId(Long productId);
    List<Review> findAllByUserId(Long userId);
    @Query("SELECT r FROM Review r WHERE r.product.tenant.id = :tenantId")
    List<Review> findAllByProductTenantId(@Param("tenantId") Long tenantId);
}

