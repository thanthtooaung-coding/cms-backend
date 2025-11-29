package com.ecommerce.ecs.features.auth.service;

import com.ecommerce.ecs.features.auth.dto.LoginRequest;
import com.ecommerce.ecs.features.auth.dto.LoginResponse;
import com.ecommerce.ecs.features.auth.dto.RegisterRequest;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    void register(RegisterRequest request);
}

