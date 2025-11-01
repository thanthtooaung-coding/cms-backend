package com.content_management_system.bms.features.auth.controller;

import com.content_management_system.bms.common.dto.ApiResponse;
import com.content_management_system.bms.features.auth.dto.*;
import com.content_management_system.bms.features.auth.service.AuthService;
import com.content_management_system.bms.features.booking.dto.MyBookingsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody final LoginRequest request) {
        try {
            final LoginResponse loginResponse = this.authService.authenticate(request.email(), request.password());
            return ApiResponse.ok(loginResponse, "Login successful");
        } catch (BadCredentialsException e) {
            return ApiResponse.unauthorized("Incorrect email or password");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@RequestBody final RegisterRequest request) {
        this.authService.register(request);
        return ApiResponse.created(null, "User registered successfully");
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMe(@AuthenticationPrincipal UserDetails userDetails) {
        return ApiResponse.ok(authService.getMe(userDetails.getUsername()), "User retrieved successfully");
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateMe(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UpdateMeRequest request
    ) {
        return ApiResponse.ok(authService.updateMe(userDetails.getUsername(), request), "User updated successfully");
    }

    @PostMapping("/me/profile-picture")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfilePicture(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("file") MultipartFile file
    ) {
        return ApiResponse.ok(authService.updateProfilePicture(userDetails.getUsername(), file), "Profile picture updated successfully");
    }

    @GetMapping("/me/bookings")
    public ResponseEntity<ApiResponse<MyBookingsResponse>> getMyBookings(@AuthenticationPrincipal UserDetails userDetails) {
        return ApiResponse.ok(authService.getMyBookings(userDetails.getUsername()), "User bookings retrieved successfully");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ApiResponse.ok(null, "Password reset OTP sent to your email.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ApiResponse.ok(null, "Password has been reset successfully.");
    }
}
