package com.movie.celestix.features.admins.service.impl;

import com.movie.celestix.common.enums.Role;
import com.movie.celestix.common.models.User;
import com.movie.celestix.common.repository.TenantRepository;
import com.movie.celestix.common.repository.jpa.UserJpaRepository;
import com.movie.celestix.features.admins.dto.AdminResponse;
import com.movie.celestix.features.admins.dto.CreateAdminRequest;
import com.movie.celestix.features.admins.dto.UpdateAdminRequest;
import com.movie.celestix.features.admins.mapper.AdminMapper;
import com.movie.celestix.features.admins.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserJpaRepository userJpaRepository;
    private final TenantRepository tenantRepository;
    private final AdminMapper adminMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public AdminResponse createAdmin(CreateAdminRequest createAdminRequest) {
        if (userJpaRepository.findByEmail(createAdminRequest.email()).isPresent()) {
            throw new IllegalStateException("Email already registered!");
        }

        User user = new User();
        user.setName(createAdminRequest.name());
        user.setEmail(createAdminRequest.email());
        user.setPassword(passwordEncoder.encode(createAdminRequest.password()));
        user.setRole(Role.ADMIN);

        // Set tenant if provided
        if (createAdminRequest.tenantId() != null) {
            com.movie.celestix.common.models.Tenant tenant = tenantRepository.findById(createAdminRequest.tenantId())
                    .orElseThrow(() -> new IllegalStateException("Tenant not found with id: " + createAdminRequest.tenantId()));
            user.setTenant(tenant);
        }

        User savedUser = userJpaRepository.save(user);
        return adminMapper.toDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminResponse> getAllAdmins(Long tenantId) {
        List<User> users = userJpaRepository.findAllByTenantId(tenantId).stream()
                .filter(user -> user.getRole() == Role.ADMIN)
                .collect(Collectors.toList());
        return adminMapper.toDtoList(users);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminResponse getAdminById(Long id, Long tenantId) {
        User user = userJpaRepository.findById(id)
                .filter(u -> u.getRole() == Role.ADMIN && u.getTenant() != null && u.getTenant().getId().equals(tenantId))
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        return adminMapper.toDto(user);
    }

    @Override
    @Transactional
    public AdminResponse updateAdmin(Long id, UpdateAdminRequest updateAdminRequest, Long tenantId) {
        User user = userJpaRepository.findById(id)
                .filter(u -> u.getRole() == Role.ADMIN && u.getTenant() != null && u.getTenant().getId().equals(tenantId))
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        user.setName(updateAdminRequest.name());
        user.setEmail(updateAdminRequest.email());

        User updatedUser = userJpaRepository.save(user);
        return adminMapper.toDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteAdmin(Long id, Long tenantId) {
        User user = userJpaRepository.findById(id)
                .filter(u -> u.getRole() == Role.ADMIN && u.getTenant() != null && u.getTenant().getId().equals(tenantId))
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        userJpaRepository.delete(user);
    }
}
