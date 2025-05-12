package br.com.moneyTracker.api.exceptions;

public class SendMailException extends RuntimeException {
    public SendMailException(String message) {
        super(message);
    }
    public SendMailException(String message, Throwable cause) {
        super(message, cause);
    }
}
