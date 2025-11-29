package com.movie.celestix.features.auth.service;

import com.movie.celestix.features.auth.dto.*;
import com.movie.celestix.features.booking.dto.MyBookingsResponse;
import org.springframework.web.multipart.MultipartFile;

public interface AuthService {
    LoginResponse authenticate(String email, String password, Long tenantId);
    void register(RegisterRequest request, Long tenantId);
    UserResponse getMe(String email);
    UserResponse updateMe(String email, UpdateMeRequest request);
    UserResponse updateProfilePicture(String email, MultipartFile file);
    MyBookingsResponse getMyBookings(String email, Long tenantId);
    void forgotPassword(ForgotPasswordRequest request, Long tenantId);
    void resetPassword(ResetPasswordRequest request, Long tenantId);
    void changePassword(String email, ChangePasswordRequest request);
}
