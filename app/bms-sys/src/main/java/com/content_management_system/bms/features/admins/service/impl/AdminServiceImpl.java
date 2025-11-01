package com.content_management_system.bms.features.admins.service.impl;

import com.content_management_system.bms.common.enums.Role;
import com.content_management_system.bms.common.models.User;
import com.content_management_system.bms.common.repository.jpa.UserJpaRepository;
import com.content_management_system.bms.features.admins.dto.AdminResponse;
import com.content_management_system.bms.features.admins.dto.CreateAdminRequest;
import com.content_management_system.bms.features.admins.dto.UpdateAdminRequest;
import com.content_management_system.bms.features.admins.mapper.AdminMapper;
import com.content_management_system.bms.features.admins.service.AdminService;
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

        User savedUser = userJpaRepository.save(user);
        return adminMapper.toDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminResponse> getAllAdmins() {
        List<User> users = userJpaRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.ADMIN)
                .collect(Collectors.toList());
        return adminMapper.toDtoList(users);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminResponse getAdminById(Long id) {
        User user = userJpaRepository.findById(id)
                .filter(u -> u.getRole() == Role.ADMIN)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        return adminMapper.toDto(user);
    }

    @Override
    @Transactional
    public AdminResponse updateAdmin(Long id, UpdateAdminRequest updateAdminRequest) {
        User user = userJpaRepository.findById(id)
                .filter(u -> u.getRole() == Role.ADMIN)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        user.setName(updateAdminRequest.name());
        user.setEmail(updateAdminRequest.email());

        User updatedUser = userJpaRepository.save(user);
        return adminMapper.toDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteAdmin(Long id) {
        User user = userJpaRepository.findById(id)
                .filter(u -> u.getRole() == Role.ADMIN)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        userJpaRepository.delete(user);
    }
}
