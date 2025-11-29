package com.ecommerce.ecs.features.user.service.impl;

import com.ecommerce.ecs.common.enums.Role;
import com.ecommerce.ecs.common.exception.ResourceNotFoundException;
import com.ecommerce.ecs.common.models.Tenant;
import com.ecommerce.ecs.common.models.User;
import com.ecommerce.ecs.common.repository.TenantRepository;
import com.ecommerce.ecs.common.repository.jpa.UserJpaRepository;
import com.ecommerce.ecs.features.user.dto.CreateUserRequest;
import com.ecommerce.ecs.features.user.dto.UpdateUserRequest;
import com.ecommerce.ecs.features.user.dto.UserResponse;
import com.ecommerce.ecs.features.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserJpaRepository userJpaRepository;
    private final TenantRepository tenantRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    @Transactional
    public UserResponse create(CreateUserRequest request, Long tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));
        
        if (userJpaRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalStateException("User with email " + request.getEmail() + " already exists");
        }
        
        Role role = Role.fromId(request.getRoleId());
        
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        user.setTenant(tenant);
        user.setAddress(request.getAddress());
        user.setPhoneNumber(request.getPhoneNumber());
        
        User saved = userJpaRepository.save(user);
        return toResponse(saved);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAll(Long tenantId) {
        return userJpaRepository.findAllByTenantId(tenantId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(Long id, Long tenantId) {
        User user = userJpaRepository.findById(id)
                .filter(u -> u.getTenant().getId().equals(tenantId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return toResponse(user);
    }
    
    @Override
    @Transactional
    public UserResponse update(Long id, UpdateUserRequest request, Long tenantId) {
        User user = userJpaRepository.findById(id)
                .filter(u -> u.getTenant().getId().equals(tenantId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (request.getName() != null) user.setName(request.getName());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getRoleId() != null) user.setRole(Role.fromId(request.getRoleId()));
        if (request.getAddress() != null) user.setAddress(request.getAddress());
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
        if (request.getProfileUrl() != null) user.setProfileUrl(request.getProfileUrl());
        
        User updated = userJpaRepository.save(user);
        return toResponse(updated);
    }
    
    @Override
    @Transactional
    public void delete(Long id, Long tenantId) {
        User user = userJpaRepository.findById(id)
                .filter(u -> u.getTenant().getId().equals(tenantId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        userJpaRepository.delete(user);
    }
    
    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .profileUrl(user.getProfileUrl())
                .address(user.getAddress())
                .phoneNumber(user.getPhoneNumber())
                .tenantId(user.getTenant().getId())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}

