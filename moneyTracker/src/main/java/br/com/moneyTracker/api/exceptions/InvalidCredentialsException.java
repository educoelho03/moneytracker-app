package br.com.moneyTracker.api.exceptions;

public class InvalidCredentialsException extends RuntimeException {
  public InvalidCredentialsException(String message) {
    super(message);
  }
  public InvalidCredentialsException(String message, Throwable cause) {
    super(message, cause);
  }
  public InvalidCredentialsException(Throwable cause) {
    super(cause);
  }
}
