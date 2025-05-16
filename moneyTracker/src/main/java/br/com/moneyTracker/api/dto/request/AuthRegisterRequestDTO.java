package br.com.moneyTracker.api.dto.request;

import br.com.moneyTracker.domain.model.enums.USER_ROLES;

public record AuthRegisterRequestDTO(String name, String email, String password, USER_ROLES roles) {

    public USER_ROLES rolesOrDefault() {
        return roles != null ? roles : USER_ROLES.ROLE_USER;
    }
}
