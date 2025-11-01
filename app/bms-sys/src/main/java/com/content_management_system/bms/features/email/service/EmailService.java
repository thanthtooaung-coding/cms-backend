package com.content_management_system.bms.features.email.service;

import com.content_management_system.bms.features.email.dto.EmailDetails;
import com.content_management_system.bms.features.email.dto.ReminderEmailDetails;

public interface EmailService {
    void sendBookingConfirmationEmail(EmailDetails emailDetails);
    void sendShowtimeReminderEmail(ReminderEmailDetails emailDetails);
    void sendOtpEmail(String to, String otp);
    void sendRefundStatusEmail(String to, String customerName, String bookingId, String status);
}
