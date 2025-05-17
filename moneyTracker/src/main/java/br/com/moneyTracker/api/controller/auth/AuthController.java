package br.com.moneyTracker.api.controller.auth;

import br.com.moneyTracker.api.dto.request.AuthLoginRequestDTO;
import br.com.moneyTracker.api.dto.request.AuthRegisterRequestDTO;
import br.com.moneyTracker.api.dto.response.DataResponseDTO;
import br.com.moneyTracker.domain.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/login")
    public ResponseEntity<DataResponseDTO> loginUser(@RequestBody AuthLoginRequestDTO body) {
        DataResponseDTO response = authService.loginUser(body);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<DataResponseDTO> registerUser(@RequestBody AuthRegisterRequestDTO body) {
        DataResponseDTO response = authService.registerUser(body);
        return ResponseEntity.ok(response);
    }
}
