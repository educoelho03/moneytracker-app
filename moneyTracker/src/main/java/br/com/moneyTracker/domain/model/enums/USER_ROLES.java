package br.com.moneyTracker.domain.model.enums;

public enum USER_ROLES {
    ROLE_USER("USER"),
    ROLE_ADMIN("ADMIN");

    private String role;

    USER_ROLES(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
