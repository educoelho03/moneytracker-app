package br.com.moneyTracker.api.controller.email;

import br.com.moneyTracker.api.dto.request.EmailRequestDTO;
import br.com.moneyTracker.interfaces.EmailInterface;
import br.com.moneyTracker.domain.service.EmailServiceSendgrid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mail")
public class EmailController {

    private final EmailServiceSendgrid emailService;

    public EmailController(EmailServiceSendgrid emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> sendEmailPasswordReset(@RequestBody @Valid EmailRequestDTO emailRequestDTO) {
        try {
            String resetPasswordLink = "http://localhost:5173/reset-password";

            String emailBody = "Hi,\n\n" +
                    "We received a request to reset your password.\n" +
                    "To proceed, please click the link below:\n\n" +
                    resetPasswordLink + "\n\n" +
                    "If you didn’t request this, you can safely ignore this email.\n\n" +
                    "Best regards,\n" +
                    "The MoneyTracker Team";

            var message = EmailInterface.Message.builder()
                    .to(emailRequestDTO.email())
                    .subject("🔐 Password Reset Request")
                    .body(emailBody)
                    .build();

            emailService.sendMail(message);
            return ResponseEntity.ok("Password reset email sent successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send password reset email.");
        }
    }
}
