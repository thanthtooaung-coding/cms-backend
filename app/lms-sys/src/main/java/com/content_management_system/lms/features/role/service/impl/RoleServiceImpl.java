package com.content_management_system.lms.features.role.service.impl;

import com.content_management_system.lms.features.role.dto.RoleResponse;
import com.content_management_system.lms.features.role.mapper.RoleMapper;
import com.content_management_system.lms.features.role.service.RoleService;
import com.content_management_system.lms.shared.constants.LmsRoleName;
import com.content_management_system.lms.shared.entity.Role;
import com.content_management_system.lms.shared.exception.ResourceNotFoundException;
import com.content_management_system.lms.shared.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> findAll() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream()
                .map(RoleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponse findByName(String name) {
        try {
            LmsRoleName roleName = LmsRoleName.valueOf(name);
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found with name: " + name));
            return RoleMapper.toResponse(role);
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Invalid role name: " + name);
        }
    }
}

