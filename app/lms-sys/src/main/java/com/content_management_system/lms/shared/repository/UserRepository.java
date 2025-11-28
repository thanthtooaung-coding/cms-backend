package com.content_management_system.lms.shared.repository;

import com.content_management_system.lms.shared.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.role.name = :roleName")
    List<User> findAllByRoleName(@Param("roleName") String roleName);

    @Query("SELECT u FROM User u WHERE u.username = :username AND u.tenant.id = :tenantId")
    Optional<User> findByUsernameAndTenantId(@Param("username") String username, @Param("tenantId") Long tenantId);
}