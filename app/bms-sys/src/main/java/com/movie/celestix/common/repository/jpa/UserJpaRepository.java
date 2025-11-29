package com.movie.celestix.common.repository.jpa;

import com.movie.celestix.common.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndTenantId(String email, Long tenantId);
    List<User> findAllByTenantId(Long tenantId);
    long countByTenantId(Long tenantId);
}
