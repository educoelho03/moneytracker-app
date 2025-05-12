package br.com.moneyTracker.api.exceptions;

public class PasswordNullOrEmptyException extends RuntimeException {
    public PasswordNullOrEmptyException(String message) {
        super(message);
    }
}
