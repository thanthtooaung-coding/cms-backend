package com.content_management_system.bms.common.config;

import com.content_management_system.bms.common.enums.Role;
import com.content_management_system.bms.common.repository.jpa.UserJpaRepository;
import com.content_management_system.bms.features.auth.dto.RegisterRequest;
import com.content_management_system.bms.features.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminUserInitializer implements CommandLineRunner {

    private final AuthService authService;
    private final UserJpaRepository userJpaRepository;

    @Override
    public void run(String... args) throws Exception {
        if (this.userJpaRepository.findByEmail("admin@celestix.com").isEmpty()) {
            final RegisterRequest admin = new RegisterRequest(
                    "Admin",
                    "admin@celestix.com",
                    "admin123",
                    Role.ADMIN
            );
            this.authService.register(admin);
        }
    }
}
