package com.ecommerce.ecs.common.repository.jpa;

import com.ecommerce.ecs.common.models.Refund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefundJpaRepository extends JpaRepository<Refund, Long> {
    @Query("SELECT r FROM Refund r WHERE r.order.tenant.id = :tenantId")
    List<Refund> findAllByOrderTenantId(@Param("tenantId") Long tenantId);
    Optional<Refund> findByOrderId(Long orderId);
}

