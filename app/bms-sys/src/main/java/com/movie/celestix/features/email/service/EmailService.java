package com.movie.celestix.features.email.service;

import com.movie.celestix.features.email.dto.EmailDetails;
import com.movie.celestix.features.email.dto.ReminderEmailDetails;

public interface EmailService {
    void sendBookingConfirmationEmail(EmailDetails emailDetails);
    void sendShowtimeReminderEmail(ReminderEmailDetails emailDetails);
    void sendOtpEmail(String to, String otp, String tenantName, String tenantLogoUrl);
    void sendRefundStatusEmail(String to, String customerName, String bookingId, String status, String tenantName, String tenantLogoUrl);
}
