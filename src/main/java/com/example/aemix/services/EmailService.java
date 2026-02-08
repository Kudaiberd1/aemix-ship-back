package com.example.aemix.services;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final Resend resend;

    public EmailService(@Value("${RESEND_API_KEY}") String apiKey) {
        this.resend = new Resend(apiKey);
    }

    public String sendVerificationEmail(String to, String subject, String text) {
        try {
            CreateEmailOptions options = CreateEmailOptions.builder()
                    .from("onboarding@resend.dev")
                    .to(to)
                    .subject(subject)
                    .html(text)
                    .build();

            CreateEmailResponse response = resend.emails().send(options);
            return response.getId();

        } catch (ResendException e) {
            throw new RuntimeException("Failed to send email via Resend: " + e.getMessage(), e);
        }
    }
}