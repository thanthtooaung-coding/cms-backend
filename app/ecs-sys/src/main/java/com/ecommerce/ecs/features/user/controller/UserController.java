package com.ecommerce.ecs.features.user.controller;

import com.ecommerce.ecs.common.dto.ApiResponse;
import com.ecommerce.ecs.features.user.dto.CreateUserRequest;
import com.ecommerce.ecs.features.user.dto.UpdateUserRequest;
import com.ecommerce.ecs.features.user.dto.UserResponse;
import com.ecommerce.ecs.features.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> create(
            @RequestBody CreateUserRequest request,
            @RequestParam Long tenantId) {
        UserResponse response = userService.create(request, tenantId);
        return ApiResponse.created(response, "User created successfully");
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAll(@RequestParam Long tenantId) {
        List<UserResponse> users = userService.getAll(tenantId);
        return ApiResponse.ok(users, "Users retrieved successfully");
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getById(
            @PathVariable Long id,
            @RequestParam Long tenantId) {
        UserResponse response = userService.getById(id, tenantId);
        return ApiResponse.ok(response, "User retrieved successfully");
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> update(
            @PathVariable Long id,
            @RequestBody UpdateUserRequest request,
            @RequestParam Long tenantId) {
        UserResponse response = userService.update(id, request, tenantId);
        return ApiResponse.ok(response, "User updated successfully");
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            @RequestParam Long tenantId) {
        userService.delete(id, tenantId);
        return ApiResponse.noContent("User deleted successfully");
    }
}

