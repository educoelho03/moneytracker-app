package br.com.moneyTracker.exceptions;

public class AmountInsufficientException extends RuntimeException {
    public AmountInsufficientException(String message) {
        super(message);
    }
}
