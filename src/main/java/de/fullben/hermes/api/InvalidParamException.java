package de.fullben.hermes.api;

/**
 * Should be thrown whenever a parameter to some service or service component is found to be
 * invalid.
 *
 * <p>At the web API level, exceptions of this type are all treated as indicators of a bad requests,
 * and are correspondingly responded to with a {@code 400} status. Note that the error response
 * object (instance of {@link de.fullben.hermes.representation.ErrorRepresentation
 * ErrorRepresentation}) will contain the message of this exception as its own message text.
 *
 * @author Benedikt Full
 */
public class InvalidParamException extends RuntimeException {

  private static final long serialVersionUID = -4472421745829297739L;

  public InvalidParamException(String msg) {
    super(msg);
  }
}
