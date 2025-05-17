package br.com.moneyTracker.api.controller.user;

import br.com.moneyTracker.api.dto.request.ResetPasswordRequestDTO;
import br.com.moneyTracker.domain.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> updatePassword(@RequestBody ResetPasswordRequestDTO passwordRequestDTO) {
        userService.updateUserPassword(passwordRequestDTO.email(), passwordRequestDTO.newPassword());
        return ResponseEntity.status(200).body("Password updated successfully.");
    }
}
