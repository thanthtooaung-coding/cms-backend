package com.content_management_system.lms.features.user.service;

import com.content_management_system.lms.features.user.dto.ChangePasswordRequest;
import com.content_management_system.lms.features.user.dto.CreateUserRequest;
import com.content_management_system.lms.features.user.dto.StudentRegistrationRequest;
import com.content_management_system.lms.features.user.dto.UpdateProfileRequest;
import com.content_management_system.lms.features.user.dto.UpdateUserRequest;
import com.content_management_system.lms.features.user.dto.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse create(CreateUserRequest request);

    UserResponse registerStudent(StudentRegistrationRequest request);

    List<UserResponse> findAll(String roleName, Long tenantId);

    UserResponse findById(Long id);

    UserResponse update(Long id, UpdateUserRequest request);

    void deleteById(Long id);

    UserResponse login(String username, String password, Long tenantId);

    UserResponse updateProfile(Long userId, UpdateProfileRequest request);

    void changePassword(Long userId, ChangePasswordRequest request);
}