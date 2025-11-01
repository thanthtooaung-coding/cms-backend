package com.content_management_system.bms.features.email.service.impl;

import com.content_management_system.bms.features.email.dto.EmailDetails;
import com.content_management_system.bms.features.email.dto.ReminderEmailDetails;
import com.content_management_system.bms.features.email.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Value("${spring.mail.username}")
    private String mailFrom;

    @Override
    @Async
    public void sendBookingConfirmationEmail(EmailDetails emailDetails) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

            Context context = new Context();
            context.setVariable("customerName", emailDetails.customerName());
            context.setVariable("bookingId", emailDetails.bookingId());
            context.setVariable("movieTitle", emailDetails.movieTitle());
            context.setVariable("theaterName", emailDetails.theaterName());
            context.setVariable("showtimeDate", emailDetails.showtimeDate());
            context.setVariable("showtimeTime", emailDetails.showtimeTime());
            context.setVariable("seats", emailDetails.seats());
            context.setVariable("totalAmount", emailDetails.totalAmount());

            String htmlContent = templateEngine.process("booking-confirmation", context);

            helper.setTo(emailDetails.customerEmail());
            helper.setFrom(mailFrom);
            helper.setSubject("Your Celestix Booking Confirmation for " + emailDetails.movieTitle());
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            logger.info("Booking confirmation email sent successfully to {}", emailDetails.customerEmail());
        } catch (MessagingException e) {
            logger.error("Failed to send booking confirmation email to {}", emailDetails.customerEmail(), e);
        }
    }

    @Override
    @Async
    public void sendShowtimeReminderEmail(ReminderEmailDetails emailDetails) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

            Context context = new Context();
            context.setVariable("customerName", emailDetails.customerName());
            context.setVariable("movieTitle", emailDetails.movieTitle());
            context.setVariable("moviePosterUrl", emailDetails.moviePosterUrl());
            context.setVariable("theaterName", emailDetails.theaterName());
            context.setVariable("showtimeTime", emailDetails.showtimeTime().format(DateTimeFormatter.ofPattern("hh:mm a")));

            String htmlContent = templateEngine.process("showtime-reminder", context);

            helper.setTo(emailDetails.customerEmail());
            helper.setFrom(mailFrom);
            helper.setSubject("⏰ Reminder: Your movie '" + emailDetails.movieTitle() + "' is starting soon!");
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            logger.info("Showtime reminder email sent successfully to {}", emailDetails.customerEmail());
        } catch (MessagingException e) {
            logger.error("Failed to send showtime reminder email to {}", emailDetails.customerEmail(), e);
        }
    }

    @Override
    @Async
    public void sendOtpEmail(String to, String otp) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

            Context context = new Context();
            context.setVariable("otp", otp);

            String htmlContent = templateEngine.process("password-reset-otp", context);

            helper.setTo(to);
            helper.setFrom(mailFrom);
            helper.setSubject("Your Celestix Password Reset Code");
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            logger.info("Password reset OTP email sent successfully to {}", to);
        } catch (MessagingException e) {
            logger.error("Failed to send password reset OTP email to {}", to, e);
        }
    }

    @Override
    @Async
    public void sendRefundStatusEmail(String to, String customerName, String bookingId, String status) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

            Context context = new Context();
            context.setVariable("customerName", customerName);
            context.setVariable("bookingId", bookingId);
            context.setVariable("status", status);

            String htmlContent = templateEngine.process("refund-status", context);

            helper.setTo(to);
            helper.setFrom(mailFrom);
            helper.setSubject("Update on Your Celestix Refund Request");
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            logger.info("Refund status email sent to {}", to);
        } catch (MessagingException e) {
            logger.error("Failed to send refund status email to {}", to, e);
        }
    }
}
