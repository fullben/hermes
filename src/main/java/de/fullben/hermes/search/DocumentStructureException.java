package de.fullben.hermes.search;

/**
 * Thrown whenever a component associated with web search document processing encounters an
 * unexpected document structure or is missing required elements.
 *
 * @author Benedikt Full
 */
public class DocumentStructureException extends SearchException {

  private static final long serialVersionUID = 5510098817523056567L;

  public DocumentStructureException(String msg) {
    super(msg);
  }
}
