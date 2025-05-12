package br.com.moneyTracker.api.dto.request;

public record ResetPasswordRequestDTO(String email, String newPassword, String confirmNewPassword) {
}
