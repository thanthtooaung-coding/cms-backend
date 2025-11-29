package com.movie.celestix.features.auth.service.impl;

import com.movie.celestix.common.jwt.JwtUtil;
import com.movie.celestix.common.models.Tenant;
import com.movie.celestix.common.models.User;
import com.movie.celestix.common.repository.TenantRepository;
import com.movie.celestix.common.repository.jdbc.UserJdbcRepository;
import com.movie.celestix.common.repository.jpa.UserJpaRepository;
import com.movie.celestix.features.auth.dto.*;
import com.movie.celestix.features.auth.service.AuthService;
import com.movie.celestix.features.booking.dto.MyBookingsResponse;
import com.movie.celestix.features.booking.service.BookingService;
import com.movie.celestix.common.service.CmsService;
import com.movie.celestix.features.email.service.EmailService;
import com.movie.celestix.features.media.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserJdbcRepository userJdbcRepository;
    private final UserJpaRepository userJpaRepository;
    private final TenantRepository tenantRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final MediaService mediaService;
    private final BookingService bookingService;
    private final EmailService emailService;
    private final CmsService cmsService;

    @Override
    public LoginResponse authenticate(final String email, final String rawPassword, final Long tenantId) {
        // Validate tenant ID is provided
        if (tenantId == null) {
            throw new IllegalArgumentException("Tenant ID is required for login");
        }
        
        // Authenticate user credentials
        this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, rawPassword));
        
        // Find user by email and tenant ID to ensure user belongs to the requested tenant
        final User user = this.userJpaRepository.findByEmailAndTenantId(email, tenantId)
                .orElseThrow(() -> new BadCredentialsException("Invalid email, password, or tenant"));
        
        // Verify user belongs to the requested tenant
        if (user.getTenant() == null || !user.getTenant().getId().equals(tenantId)) {
            throw new BadCredentialsException("User does not belong to the specified tenant");
        }
        
        final UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);
        final String token = this.jwtUtil.generateToken(userDetails);
        return new LoginResponse(token, user.getRole());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(final RegisterRequest request, final Long tenantId) {
        if (tenantId == null) {
            throw new IllegalArgumentException("Tenant ID is required for registration");
        }
        
        // Check if email already exists for this tenant
        if (this.userJpaRepository.findByEmailAndTenantId(request.email(), tenantId).isPresent()) {
            throw new IllegalStateException("Email already registered for this tenant!");
        }

        final String hashedPassword = this.passwordEncoder.encode(request.password());

        // Fetch the tenant entity
        final Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found with ID: " + tenantId));

        User user = new User();
        user.setName(request.name());
        user.setPassword(hashedPassword);
        user.setEmail(request.email());
        user.setRole(request.role());
        user.setTenant(tenant);
        this.userJpaRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getMe(String email) {
        User user = userJpaRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.getProfileUrl());
    }

    @Override
    @Transactional
    public UserResponse updateMe(String email, UpdateMeRequest request) {
        User user = userJpaRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        if (request.name() != null) {
            user.setName(request.name());
        }

        User updatedUser = userJpaRepository.save(user);
        return new UserResponse(updatedUser.getId(), updatedUser.getName(), updatedUser.getEmail(), updatedUser.getRole(), updatedUser.getProfileUrl());
    }

    @Transactional
    @Override
    public UserResponse updateProfilePicture(String email, MultipartFile file) {
        final User user = userJpaRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        final String profileUrl = mediaService.store(file);
        user.setProfileUrl(profileUrl);
        final User updatedUser = userJpaRepository.save(user);
        return new UserResponse(updatedUser.getId(), updatedUser.getName(), updatedUser.getEmail(), updatedUser.getRole(), updatedUser.getProfileUrl());
    }

    @Override
    public MyBookingsResponse getMyBookings(String email, Long tenantId) {
        return bookingService.retrieveMyBookings(email, tenantId);
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request, Long tenantId) {
        User user = userJpaRepository.findByEmailAndTenantId(request.email(), tenantId)
                .orElseThrow(() -> new IllegalArgumentException("User with email " + request.email() + " not found."));

        String otp = String.format("%06d", new Random().nextInt(999999));
        user.setOtp(otp);
        user.setOtpGeneratedTime(LocalDateTime.now());
        userJpaRepository.save(user);

        final com.movie.celestix.common.models.Tenant tenant = user.getTenant();
        final String tenantName = tenant != null ? tenant.getName() : null;
        // Get logo URL from CMS page_request table
        String tenantLogoUrl = null;
        if (tenantName != null) {
            CmsService.TenantInfo tenantInfo = cmsService.getTenantInfoByName(tenantName);
            if (tenantInfo != null) {
                tenantLogoUrl = tenantInfo.getLogoUrl();
            }
        }
        emailService.sendOtpEmail(user.getEmail(), otp, tenantName, tenantLogoUrl);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request, Long tenantId) {
        User user = userJpaRepository.findByEmailAndTenantId(request.email(), tenantId)
                .orElseThrow(() -> new IllegalArgumentException("User with email " + request.email() + " not found."));

        if (user.getOtp() == null || user.getOtpGeneratedTime() == null || !user.getOtp().equals(request.otp())) {
            throw new IllegalArgumentException("Invalid OTP.");
        }

        long minutes = Duration.between(user.getOtpGeneratedTime(), LocalDateTime.now()).toMinutes();
        if (minutes > 5) {
            throw new IllegalArgumentException("OTP has expired.");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setOtp(null);
        user.setOtpGeneratedTime(null);
        userJpaRepository.save(user);
    }

    @Override
    @Transactional
    public void changePassword(String email, ChangePasswordRequest request) {
        User user = userJpaRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Verify current password
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new BadCredentialsException("Current password is incorrect");
        }

        // Validate new password
        if (request.newPassword() == null || request.newPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("New password cannot be empty");
        }

        if (request.newPassword().length() < 6) {
            throw new IllegalArgumentException("New password must be at least 6 characters long");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userJpaRepository.save(user);
    }
}
