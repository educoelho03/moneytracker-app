package br.com.moneyTracker.controller.user;

import br.com.moneyTracker.dto.request.ResetPasswordRequestDTO;
import br.com.moneyTracker.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Tag(name = "Usuario", description = "Controlador para atualizar a senha do usuario")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Atualiza a senha do usuario", description = "Método para alterar/atualizar a senha do usuario")
    @ApiResponse(responseCode = "200", description = "Senha alterada com sucesso")
    @ApiResponse(responseCode = "400", description = "Erro ao atualizar a senha")
    @ApiResponse(responseCode = "500", description = "Erro no servidor")
    public ResponseEntity<String> updatePassword(@RequestBody ResetPasswordRequestDTO passwordRequestDTO) {
        userService.updateUserPassword(passwordRequestDTO.email(), passwordRequestDTO.newPassword());
        return ResponseEntity.ok("Senha atualizada com sucesso!");
    }
}
