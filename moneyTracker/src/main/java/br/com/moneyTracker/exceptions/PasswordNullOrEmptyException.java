package br.com.moneyTracker.exceptions;

public class PasswordNullOrEmptyException extends RuntimeException {
    public PasswordNullOrEmptyException(String message) {
        super(message);
    }
}
