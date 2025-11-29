package com.movie.celestix.common.repository.jpa;

import com.movie.celestix.common.models.Theater;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TheaterJpaRepository extends JpaRepository<Theater, Long> {
    Optional<Theater> findByName(String name);
    List<Theater> findAllByTenantId(Long tenantId);
    Optional<Theater> findByNameAndTenantId(String name, Long tenantId);
    long countByTenantId(Long tenantId);
}
