package de.fullben.hermes.search;

import de.fullben.hermes.representation.SearchResultRepresentation;
import java.util.List;
import org.jsoup.nodes.Document;

/**
 * Implementations of this interface can process the result page of a specific web search
 * implementation (e.g., Google search).
 *
 * @author Benedikt Full
 */
public interface SearchResultParser {

  /**
   * Attempts to parse search results from the given document.
   *
   * @param doc a search result page
   * @return a list containing all parsable results found in the given document, may be empty
   * @throws SearchException if an error occurs during parsing
   */
  List<SearchResultRepresentation> parse(Document doc) throws SearchException;
}
