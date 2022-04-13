package de.fullben.hermes.search.google;

import static de.fullben.hermes.util.Preconditions.notNull;

import de.fullben.hermes.representation.SearchResultRepresentation;
import de.fullben.hermes.search.SearchException;
import java.util.List;
import java.util.stream.Collectors;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Parser for converting a {@link Document} representing a Google web search result page to a more
 * suitable data representation.
 *
 * @author Benedikt Full
 */
public class GoogleSearchResultParser {

  private static final String DIV_CLASSES_RESULT_ITEM = "ZINbbc luh4tb xpd O9g5cc uUPGi";
  private static final String DIV_CLASSES_PAGE_HIERARCHY = "BNeawe UPmit AP7Wnd";
  private static final String DIV_CLASSES_TITLE = "BNeawe vvjwJb AP7Wnd";
  private static final String DIV_CLASSES_SNIPPET = "BNeawe s3v9rd AP7Wnd";

  public GoogleSearchResultParser() {}

  /**
   * Finds and returns all Google web search results found in the given document that match the
   * formatting (based on {@code div} classes) expected by the parser.
   *
   * @param doc a document, not {@code null}
   * @return a list of all parsable results found
   * @throws SearchException if an error occurs while trying to parse the document
   */
  public List<SearchResultRepresentation> parse(Document doc) throws SearchException {
    notNull(doc);
    try {
      return doc.select(divClasses(DIV_CLASSES_RESULT_ITEM)).stream()
          .map(this::parseResult)
          .collect(Collectors.toList());
    } catch (Exception e) {
      // Gotta catch 'em all: not really best practice, but it's very likely that this will fail
      // eventually (e.g., due to Google changing its site layout, or simply make a new obfuscation
      // run on their HTML class names, so on...), therefore we wrap any error in an exception type
      // that can be handled reliably further up the call chain
      throw new SearchException(
          "An error occurred while trying to parse a Google search result", e);
    }
  }

  private SearchResultRepresentation parseResult(Element result) {
    SearchResultRepresentation res = new SearchResultRepresentation();
    res.setTitle(parseResultTitle(result));
    res.setSnippet(parseResultSnippet(result));
    res.setUrl(parseResultUrl(result));
    res.setPageHierarchy(parseResultPageHierarchy(result));
    return res;
  }

  private String parseResultTitle(Element result) {
    return firstElementChildText(result, divClasses(DIV_CLASSES_TITLE));
  }

  private String parseResultUrl(Element result) {
    Element link = result.select("a[href]").first();
    if (link == null) {
      return null;
    }

    String url = link.attr("href");
    if (url.isBlank()) {
      return null;
    }

    return actualUrl(url);
  }

  private String actualUrl(String enrichedUrl) {
    String urlPrefix = "/url?q=";
    String urlPostfixStart = "&sa=";
    String url = enrichedUrl.substring(enrichedUrl.indexOf(urlPrefix) + urlPrefix.length());
    url = url.substring(0, url.indexOf(urlPostfixStart));
    return url;
  }

  private String parseResultPageHierarchy(Element result) {
    return firstElementChildText(result, divClasses(DIV_CLASSES_PAGE_HIERARCHY));
  }

  private String parseResultSnippet(Element result) {
    return firstElementChildText(result, divClasses(DIV_CLASSES_SNIPPET));
  }

  private String firstElementChildText(Element element, String selector) {
    Element match = element.select(selector).first();
    return match == null ? null : match.text();
  }

  private String divClasses(String classAttribute) {
    // Spaces in class names are considered separate class names
    return "div." + classAttribute.replaceAll(" ", ".");
  }
}
