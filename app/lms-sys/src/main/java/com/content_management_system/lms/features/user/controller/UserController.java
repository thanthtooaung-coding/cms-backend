package com.content_management_system.lms.features.user.controller;

import com.content_management_system.lms.features.user.dto.ChangePasswordRequest;
import com.content_management_system.lms.features.user.dto.CreateUserRequest;
import com.content_management_system.lms.features.user.dto.StudentRegistrationRequest;
import com.content_management_system.lms.features.user.dto.UpdateProfileRequest;
import com.content_management_system.lms.features.user.dto.UpdateUserRequest;
import com.content_management_system.lms.features.user.dto.UserResponse;
import com.content_management_system.lms.features.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody CreateUserRequest request) {
        UserResponse response = userService.create(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerStudent(@RequestBody StudentRegistrationRequest request) {
        UserResponse response = userService.registerStudent(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Long tenantId) {
        List<UserResponse> responses = userService.findAll(role, tenantId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse response = userService.findById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        UserResponse response = userService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/profile")
    public ResponseEntity<UserResponse> updateProfile(
            @PathVariable Long id,
            @RequestBody UpdateProfileRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        // Only allow users to update their own profile
        if (userId != null && !userId.equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        UserResponse response = userService.updateProfile(id, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long id,
            @RequestBody ChangePasswordRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        // Only allow users to change their own password
        if (userId != null && !userId.equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        userService.changePassword(id, request);
        return ResponseEntity.noContent().build();
    }
}
