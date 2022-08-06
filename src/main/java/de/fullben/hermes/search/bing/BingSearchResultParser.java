package de.fullben.hermes.search.bing;

import static de.fullben.hermes.util.Preconditions.notNull;

import de.fullben.hermes.representation.SearchResultRepresentation;
import de.fullben.hermes.search.DocumentStructureException;
import de.fullben.hermes.search.SearchException;
import de.fullben.hermes.search.SearchResultParser;
import java.util.List;
import java.util.stream.Collectors;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Parser for converting a {@link Document} representing a Bing web search result page to a more
 * suitable data representation.
 *
 * @author Benedikt Full
 */
public class BingSearchResultParser implements SearchResultParser {

  private static final String ID_RESULT_LIST = "b_results";
  private static final String DIV_CLASS_RESULT_ITEM = "b_algo";

  public BingSearchResultParser() {}

  /**
   * Finds and returns all Bing web search results found in the result list element located in the
   * given search result page.
   *
   * @param doc a document, not {@code null}
   * @return a list of all parsable results found
   * @throws SearchException if an error occurs while trying to parse the document
   * @throws DocumentStructureException if the given document does not contain a Bing search result
   *     list element
   */
  @Override
  public List<SearchResultRepresentation> parse(Document doc) throws SearchException {
    notNull(doc);
    Elements resultElements = findResultElements(doc);
    try {
      return resultElements.stream()
          .filter(this::isParsableResult)
          .map(this::parseResult)
          .collect(Collectors.toList());
    } catch (Exception e) {
      // Gotta catch 'em all: not really best practice, but it's very likely that this will fail
      // eventually (e.g., due to Bing changing its site layout), therefore we wrap any error in an
      // exception type that can be handled reliably further up the call chain
      throw new SearchException("An error occurred while trying to parse a Bing search result", e);
    }
  }

  private Elements findResultElements(Document doc) throws DocumentStructureException {
    Element resultsContainer = doc.getElementById(ID_RESULT_LIST);
    if (resultsContainer == null) {
      throw new DocumentStructureException(
          "Document does not contain Bing search results container element with id '"
              + ID_RESULT_LIST
              + "'");
    }
    return resultsContainer.children();
  }

  private boolean isParsableResult(Element result) {
    return result.className().equals(DIV_CLASS_RESULT_ITEM);
  }

  private SearchResultRepresentation parseResult(Element result) {
    SearchResultRepresentation res = new SearchResultRepresentation();
    res.setTitle(parseResultTitle(result));
    res.setSnippet(parseResultSnippet(result));
    res.setUrl(parseResultUrl(result));
    // Set this to null explicitly, because Bing does not provide page hierarchy information
    res.setPageHierarchy(null);
    return res;
  }

  private String parseResultTitle(Element result) {
    Element title = findTitle(result);
    if (title == null) {
      return null;
    }

    return title.text();
  }

  private String parseResultUrl(Element result) {
    Element title = findTitle(result);
    if (title == null) {
      return null;
    }

    String url = title.attr("href");
    if (url.isBlank()) {
      return null;
    }

    return url;
  }

  private String parseResultSnippet(Element result) {
    Element snippet = result.select("p").first();
    if (snippet == null) {
      return null;
    }
    return snippet.text();
  }

  private Element findTitle(Element result) {
    return result.select("a[href]").first();
  }
}
