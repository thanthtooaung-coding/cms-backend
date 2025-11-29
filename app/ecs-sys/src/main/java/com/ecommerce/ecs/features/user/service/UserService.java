package com.ecommerce.ecs.features.user.service;

import com.ecommerce.ecs.features.user.dto.CreateUserRequest;
import com.ecommerce.ecs.features.user.dto.UpdateUserRequest;
import com.ecommerce.ecs.features.user.dto.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse create(CreateUserRequest request, Long tenantId);
    List<UserResponse> getAll(Long tenantId);
    UserResponse getById(Long id, Long tenantId);
    UserResponse update(Long id, UpdateUserRequest request, Long tenantId);
    void delete(Long id, Long tenantId);
}

