package com.content_management_system.lms.features.user.service.impl;

import com.content_management_system.lms.features.user.dto.CreateUserRequest;
import com.content_management_system.lms.features.user.dto.UpdateUserRequest;
import com.content_management_system.lms.features.user.dto.UserResponse;
import com.content_management_system.lms.features.user.mapper.UserMapper;
import com.content_management_system.lms.features.user.service.UserService;
import com.content_management_system.lms.shared.constants.LmsRoleName;
import com.content_management_system.lms.shared.entity.Role;
import com.content_management_system.lms.shared.entity.Tenant;
import com.content_management_system.lms.shared.entity.User;
import com.content_management_system.lms.shared.exception.ResourceNotFoundException;
import com.content_management_system.lms.shared.exception.UnauthorizedException;
import com.content_management_system.lms.shared.repository.RoleRepository;
import com.content_management_system.lms.shared.repository.TenantRepository;
import com.content_management_system.lms.shared.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TenantRepository tenantRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse create(CreateUserRequest request) {
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + request.getRoleId()));

        Tenant tenant = tenantRepository.findById(request.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + request.getTenantId()));

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setAddress(request.getAddress());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRegistrationDate(OffsetDateTime.now());
        user.setRole(role);
        user.setTenant(tenant);

        User savedUser = userRepository.save(user);
        return UserMapper.toResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> findAll(String roleName, Long tenantId) {
        List<User> users;
        
        if (tenantId != null) {
            // Filter by tenant
            if (roleName != null && !roleName.isEmpty()) {
                users = userRepository.findAllByRoleName(roleName).stream()
                        .filter(user -> user.getTenant() != null && user.getTenant().getId().equals(tenantId))
                        .collect(Collectors.toList());
            } else {
                users = userRepository.findAll().stream()
                        .filter(user -> user.getTenant() != null && user.getTenant().getId().equals(tenantId))
                        .collect(Collectors.toList());
            }
        } else {
            // No tenant filter
            if (roleName != null && !roleName.isEmpty()) {
                users = userRepository.findAllByRoleName(roleName);
            } else {
                users = userRepository.findAll();
            }
        }
        
        return users.stream()
                .map(UserMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return UserMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse update(Long id, UpdateUserRequest request) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (request.getRoleId() != null) {
            Role newRole = roleRepository.findById(request.getRoleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + request.getRoleId()));
            existingUser.setRole(newRole);
        }

        existingUser.setName(request.getName());
        existingUser.setAddress(request.getAddress());
        existingUser.setPhoneNumber(request.getPhoneNumber());

        User updatedUser = userRepository.save(existingUser);
        return UserMapper.toResponse(updatedUser);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse login(String username, String password, Long tenantId) {
        User user = userRepository.findByUsernameAndTenantId(username, tenantId)
                .orElseThrow(() -> new UnauthorizedException("Invalid username or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UnauthorizedException("Invalid username or password");
        }

        return UserMapper.toResponse(user);
    }
}