package com.content_management_system.lms.shared.repository;

import com.content_management_system.lms.shared.constants.LmsRoleName;
import com.content_management_system.lms.shared.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    boolean existsByName(LmsRoleName name);
}