package com.example.aemix.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final ResourceLoader resourceLoader;

    public void sendVerificationEmail(String to, String link, long ttl) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(message, true, "UTF-8");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        Resource resource = resourceLoader.getResource(
                "classpath:templates/email-verification.html"
        );

        String html = null;
        try {
            html = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        html = html.replace("${link}", link)
                .replace("${ttl}", String.valueOf(ttl));

        try {
            helper.setTo(to);
            helper.setSubject("Email Verification – Aemix");
            helper.setText(html, true);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        mailSender.send(message);
    }
}


