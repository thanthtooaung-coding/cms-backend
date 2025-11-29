package com.ecommerce.ecs.features.auth.service.impl;

import com.ecommerce.ecs.common.enums.Role;
import com.ecommerce.ecs.common.exception.ResourceNotFoundException;
import com.ecommerce.ecs.common.jwt.JwtUtil;
import com.ecommerce.ecs.common.models.Tenant;
import com.ecommerce.ecs.common.models.User;
import com.ecommerce.ecs.common.repository.TenantRepository;
import com.ecommerce.ecs.common.repository.jpa.UserJpaRepository;
import com.ecommerce.ecs.features.auth.dto.LoginRequest;
import com.ecommerce.ecs.features.auth.dto.LoginResponse;
import com.ecommerce.ecs.features.auth.dto.RegisterRequest;
import com.ecommerce.ecs.features.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService, UserDetailsService {

    private final UserJpaRepository userJpaRepository;
    private final TenantRepository tenantRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userJpaRepository.findByEmailAndTenantId(request.email(), request.tenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ResourceNotFoundException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(this.loadUserByUsername(user.getEmail()));

        return new LoginResponse(
                token,
                user.getRole(),
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    @Override
    @Transactional
    public void register(RegisterRequest request) {
        if (userJpaRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalStateException("User with email " + request.email() + " already exists");
        }

        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        Role role = Role.fromId(request.roleId());

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(role);
        user.setTenant(tenant);
        user.setAddress(request.address());
        user.setPhoneNumber(request.phoneNumber());

        userJpaRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userJpaRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRole().name())
                .build();
    }
}

