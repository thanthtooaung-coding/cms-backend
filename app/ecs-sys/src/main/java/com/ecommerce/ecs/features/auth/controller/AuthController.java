package com.ecommerce.ecs.features.auth.controller;

import com.ecommerce.ecs.common.dto.ApiResponse;
import com.ecommerce.ecs.features.auth.dto.LoginRequest;
import com.ecommerce.ecs.features.auth.dto.LoginResponse;
import com.ecommerce.ecs.features.auth.dto.RegisterRequest;
import com.ecommerce.ecs.features.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ApiResponse.ok(response, "Login successful");
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return ApiResponse.created(null, "User registered successfully");
    }
}

