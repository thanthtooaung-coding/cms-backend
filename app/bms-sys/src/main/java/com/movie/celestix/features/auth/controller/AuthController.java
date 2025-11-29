package com.movie.celestix.features.auth.controller;

import com.movie.celestix.common.dto.ApiResponse;
import com.movie.celestix.common.jwt.JwtUtil;
import com.movie.celestix.features.auth.dto.*;
import com.movie.celestix.features.auth.service.AuthService;
import com.movie.celestix.features.booking.dto.MyBookingsResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody final LoginRequest request) {
        try {
            final LoginResponse loginResponse = this.authService.authenticate(request.email(), request.password(), request.tenantId());
            return ApiResponse.ok(loginResponse, "Login successful");
        } catch (BadCredentialsException e) {
            return ApiResponse.unauthorized("Incorrect email or password");
        } catch (IllegalArgumentException e) {
            return ApiResponse.unauthorized(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(
            @RequestBody final RegisterRequest request,
            @RequestParam Long tenantId
    ) {
        this.authService.register(request, tenantId);
        return ApiResponse.created(null, "User registered successfully");
    }

    private String extractEmailFromToken(HttpServletRequest request) {
        final String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            final String jwt = authorizationHeader.substring(7);
            return jwtUtil.extractUsername(jwt);
        }
        throw new RuntimeException("Authorization token is required");
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMe(HttpServletRequest request) {
        String email = extractEmailFromToken(request);
        return ApiResponse.ok(authService.getMe(email), "User retrieved successfully");
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateMe(
            HttpServletRequest request,
            @RequestBody UpdateMeRequest updateRequest
    ) {
        String email = extractEmailFromToken(request);
        return ApiResponse.ok(authService.updateMe(email, updateRequest), "User updated successfully");
    }

    @PostMapping("/me/profile-picture")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfilePicture(
            HttpServletRequest request,
            @RequestParam("file") MultipartFile file
    ) {
        String email = extractEmailFromToken(request);
        return ApiResponse.ok(authService.updateProfilePicture(email, file), "Profile picture updated successfully");
    }

    @GetMapping("/me/bookings")
    public ResponseEntity<ApiResponse<MyBookingsResponse>> getMyBookings(
            HttpServletRequest request,
            @RequestParam Long tenantId
    ) {
        String email = extractEmailFromToken(request);
        return ApiResponse.ok(authService.getMyBookings(email, tenantId), "User bookings retrieved successfully");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(
            @RequestBody ForgotPasswordRequest request,
            @RequestParam Long tenantId
    ) {
        authService.forgotPassword(request, tenantId);
        return ApiResponse.ok(null, "Password reset OTP sent to your email.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @RequestBody ResetPasswordRequest request,
            @RequestParam Long tenantId
    ) {
        authService.resetPassword(request, tenantId);
        return ApiResponse.ok(null, "Password has been reset successfully.");
    }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            HttpServletRequest request,
            @RequestBody ChangePasswordRequest changePasswordRequest
    ) {
        String email = extractEmailFromToken(request);
        try {
            authService.changePassword(email, changePasswordRequest);
            return ApiResponse.ok(null, "Password changed successfully.");
        } catch (BadCredentialsException e) {
            return ApiResponse.unauthorized(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        }
    }
}
