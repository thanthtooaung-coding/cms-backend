package com.content_management_system.lms.shared.config;

import com.content_management_system.lms.shared.constants.LmsRoleName;
import com.content_management_system.lms.shared.entity.Role;
import com.content_management_system.lms.shared.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        createRoleIfNotFound(LmsRoleName.Owner);
        createRoleIfNotFound(LmsRoleName.Admin);
        createRoleIfNotFound(LmsRoleName.Staff);
    }

    private void createRoleIfNotFound(LmsRoleName roleName) {
        if (!roleRepository.existsByName(roleName)) {
            Role role = new Role();
            role.setName(roleName);
            roleRepository.save(role);
        }
    }
}