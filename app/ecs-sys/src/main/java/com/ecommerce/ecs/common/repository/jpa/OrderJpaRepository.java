package com.ecommerce.ecs.common.repository.jpa;

import com.ecommerce.ecs.common.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderJpaRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByUserId(Long userId);
    List<Order> findAllByTenantId(Long tenantId);
    Optional<Order> findByOrderNumber(String orderNumber);
    long countByTenantId(Long tenantId);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.user.tenant.id = :tenantId")
    long countByUserTenantId(@Param("tenantId") Long tenantId);
}

