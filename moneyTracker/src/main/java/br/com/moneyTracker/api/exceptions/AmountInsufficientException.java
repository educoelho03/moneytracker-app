package br.com.moneyTracker.api.exceptions;

public class AmountInsufficientException extends RuntimeException {
    public AmountInsufficientException(String message) {
        super(message);
    }
}
