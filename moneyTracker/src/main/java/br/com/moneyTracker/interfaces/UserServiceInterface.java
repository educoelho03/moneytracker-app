package br.com.moneyTracker.interfaces;

import br.com.moneyTracker.domain.model.entities.User;
import br.com.moneyTracker.api.dto.request.AuthRegisterRequestDTO;

public interface UserServiceInterface {
    void updateUserPassword(String email, String newPassword);
    User registerUser(AuthRegisterRequestDTO authRegisterRequestDTO);
}
