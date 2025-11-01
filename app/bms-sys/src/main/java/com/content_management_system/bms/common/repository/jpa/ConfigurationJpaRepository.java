package com.content_management_system.bms.common.repository.jpa;

import com.content_management_system.bms.common.models.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfigurationJpaRepository extends JpaRepository<Configuration, Long> {
    Optional<Configuration> findByCode(String code);
}
