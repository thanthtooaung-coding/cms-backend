package com.content_management_system.bms.common.repository.jpa;

import com.content_management_system.bms.common.models.Theater;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TheaterJpaRepository extends JpaRepository<Theater, Long> {
    Optional<Theater> findByName(String name);
}
