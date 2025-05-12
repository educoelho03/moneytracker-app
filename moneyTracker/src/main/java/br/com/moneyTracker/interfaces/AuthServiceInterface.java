package br.com.moneyTracker.interfaces;

import br.com.moneyTracker.api.dto.request.AuthLoginRequestDTO;
import br.com.moneyTracker.api.dto.request.AuthRegisterRequestDTO;
import br.com.moneyTracker.api.dto.response.DataResponseDTO;

public interface AuthServiceInterface {
    DataResponseDTO loginUser(AuthLoginRequestDTO authLoginRequestDTO);
    DataResponseDTO registerUser(AuthRegisterRequestDTO authRegisterRequestDTO);
}
