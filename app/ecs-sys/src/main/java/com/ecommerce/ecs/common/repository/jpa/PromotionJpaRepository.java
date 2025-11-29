package com.ecommerce.ecs.common.repository.jpa;

import com.ecommerce.ecs.common.models.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionJpaRepository extends JpaRepository<Promotion, Long> {
    List<Promotion> findAllByTenantId(Long tenantId);
    Optional<Promotion> findByCodeAndTenantId(String code, Long tenantId);
}

