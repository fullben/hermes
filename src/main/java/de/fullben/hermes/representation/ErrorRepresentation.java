package de.fullben.hermes.representation;

import static de.fullben.hermes.util.Preconditions.notNull;

import org.springframework.http.HttpStatus;

/**
 * Represents an error.
 *
 * <p>The primary purpose of this class is to provide a standardized format for communicating errors
 * to clients. All errors encountered at the web API level that need to be communicated to calling
 * clients should use this class.
 *
 * @author Benedikt Full
 */
public class ErrorRepresentation {

  private int code;
  private String message;

  public ErrorRepresentation(int code, String message) {
    this.code = validHttpStatusCode(code);
    this.message = message;
  }

  public ErrorRepresentation(int code) {
    this(code, null);
  }

  public ErrorRepresentation(HttpStatus status, String message) {
    this(notNull(status).value(), message);
  }

  public ErrorRepresentation(HttpStatus status) {
    this(status, null);
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = validHttpStatusCode(code);
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  private static int validHttpStatusCode(int code) {
    HttpStatus status = HttpStatus.resolve(code);
    if (status == null) {
      throw new IllegalArgumentException("Unknown HTTP status code: " + code);
    }
    return status.value();
  }
}
