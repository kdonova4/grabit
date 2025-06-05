package com.kdonova4.grabit.domain;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.util.Properties;

public class EmailService {
    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendConfirmationEmail(String toEmail, int code) {

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("GrabIt Account Verification Code");
            helper.setText("You verification code is: " + code, false);
            helper.setFrom("noreply@grabit.com");

            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new IllegalStateException("Failed to send email to " + toEmail, e);
        }
    }
}
