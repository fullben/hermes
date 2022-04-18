package de.fullben.hermes.search.bing;

import static de.fullben.hermes.util.Preconditions.notNull;

import de.fullben.hermes.representation.SearchResultRepresentation;
import de.fullben.hermes.search.SearchException;
import de.fullben.hermes.search.SearchResultParser;
import java.util.List;
import java.util.stream.Collectors;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class BingSearchResultParser implements SearchResultParser {

  private static final String ID_RESULT_LIST = "b_results";
  private static final String DIV_CLASS_RESULT_ITEM = "b_algo";

  public BingSearchResultParser() {}

  @Override
  public List<SearchResultRepresentation> parse(Document doc) throws SearchException {
    notNull(doc);
    try {
      return doc.getElementById(ID_RESULT_LIST).children().stream()
          .filter(this::isParsableResult)
          .map(this::parseResult)
          .collect(Collectors.toList());
    } catch (Exception e) {
      // Gotta catch 'em all: not really best practice, but it's very likely that this will fail
      // eventually (e.g., due to Bing changing its site layout, or simply make an obfuscation
      // run on their HTML class names, so on...), therefore we wrap any error in an exception type
      // that can be handled reliably further up the call chain
      throw new SearchException("An error occurred while trying to parse a Bing search result", e);
    }
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
