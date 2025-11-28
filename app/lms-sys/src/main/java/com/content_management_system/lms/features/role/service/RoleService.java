package com.content_management_system.lms.features.role.service;

import com.content_management_system.lms.features.role.dto.RoleResponse;

import java.util.List;

public interface RoleService {
    List<RoleResponse> findAll();
    RoleResponse findByName(String name);
}

