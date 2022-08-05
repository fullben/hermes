package de.fullben.hermes.search;

/**
 * Thrown whenever a component associated with web search execution, processing, or parsing
 * encounters an error.
 *
 * @author Benedikt Full
 */
public class SearchException extends Exception {

  private static final long serialVersionUID = -418642712481221764L;

  public SearchException(String msg) {
    super(msg);
  }

  public SearchException(Throwable cause) {
    super(cause);
  }

  public SearchException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
