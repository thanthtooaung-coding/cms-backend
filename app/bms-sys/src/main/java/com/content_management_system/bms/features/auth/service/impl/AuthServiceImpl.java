package com.content_management_system.bms.features.auth.service.impl;

import com.content_management_system.bms.common.jwt.JwtUtil;
import com.content_management_system.bms.common.models.User;
import com.content_management_system.bms.common.repository.jdbc.UserJdbcRepository;
import com.content_management_system.bms.common.repository.jpa.UserJpaRepository;
import com.content_management_system.bms.features.auth.dto.*;
import com.content_management_system.bms.features.auth.service.AuthService;
import com.content_management_system.bms.features.booking.dto.MyBookingsResponse;
import com.content_management_system.bms.features.booking.service.BookingService;
import com.content_management_system.bms.features.email.service.EmailService;
import com.content_management_system.bms.features.media.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
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
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final MediaService mediaService;
    private final BookingService bookingService;
    private final EmailService emailService;

    @Override
    public LoginResponse authenticate(final String email, final String rawPassword) {
        this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, rawPassword));
        final UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);
        final User user = this.userJpaRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        final String token = this.jwtUtil.generateToken(userDetails);
        return new LoginResponse(token, user.getRole());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(final RegisterRequest request) {
        if (this.userJdbcRepository.existsByEmail(request.email())) {
            throw new IllegalStateException("Email already registered!");
        }

        final String hashedPassword = this.passwordEncoder.encode(request.password());

        this.userJpaRepository.save(
                new User(
                        request.name(),
                        hashedPassword,
                        request.email(),
                        request.role(),
                        null,
                        null,
                        null
                )
        );
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
    public MyBookingsResponse getMyBookings(String email) {
        return bookingService.retrieveMyBookings(email);
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userJpaRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("User with email " + request.email() + " not found."));

        String otp = String.format("%06d", new Random().nextInt(999999));
        user.setOtp(otp);
        user.setOtpGeneratedTime(LocalDateTime.now());
        userJpaRepository.save(user);

        emailService.sendOtpEmail(user.getEmail(), otp);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        User user = userJpaRepository.findByEmail(request.email())
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
}
