package de.fullben.hermes.search;

import static de.fullben.hermes.util.Preconditions.greaterThan;
import static de.fullben.hermes.util.Preconditions.notBlank;
import static org.apache.logging.log4j.util.Unbox.box;

import de.fullben.hermes.search.WebSearchClientBuilder.Builder;
import de.fullben.hermes.search.WebSearchClientBuilder.FirstStep;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Client for executing web search requests against the web UI of specific web search providers,
 * such as Google.
 *
 * @author Benedikt Full
 */
public class WebSearchClient {

  private static final Logger LOG = LogManager.getLogger(WebSearchClient.class);
  private final String searchUrl;
  private final String queryParam;
  private final String resultCountParam;
  private final int maxResultsPerPage;
  private final String userAgent;
  private Connection connection;

  public WebSearchClient(
      String searchUrl,
      String queryParam,
      String resultCountParam,
      int maxResultsPerPage,
      String userAgent) {
    this.searchUrl = notBlank(searchUrl);
    this.queryParam = notBlank(queryParam);
    this.resultCountParam = notBlank(resultCountParam);
    this.maxResultsPerPage = greaterThan(0, maxResultsPerPage);
    this.userAgent = notBlank(userAgent);
    connection = null;
  }

  public WebSearchClient(
      String searchUrl, String queryParam, String resultCountParam, int maxResultsPerPage) {
    this(
        searchUrl,
        queryParam,
        resultCountParam,
        maxResultsPerPage,
        "ExampleBot 2.0 (+http://example.com/bot)");
  }

  /**
   * Creates and returns a step builder for convenient creation of web search clients.
   *
   * @return a new builder instance
   */
  public static FirstStep builder() {
    return new Builder();
  }

  /**
   * Uses the web search configured for this client to return a web document containing the result
   * data for the provided query. The document will at most contain {@code maxResults} result items.
   *
   * <p><b>Note: </b>As this implementation only returns a single web document, the number of search
   * results retrievable via this method is limited to the maximum number of results per page
   * supported by the configured web search implementation. Google web search for example supports
   * no more than 100 results per page. Therefore, this method will never return a document
   * containing more than 100 results if Google is configured as web search implementation, even if
   * the provided value for {@code maxResults} exceeds 100.
   *
   * @param query the search term, usually case-insensitive
   * @param maxResults the maximum number of results contained within the returned document
   * @return the web search result page
   * @throws SearchException if an error occurs while executing the search
   */
  public Document search(String query, int maxResults) throws SearchException {
    long startTime = System.currentTimeMillis();
    notBlank(query);
    greaterThan(0, maxResults);
    if (maxResults >= (maxResultsPerPage - 4)) {
      LOG.warn(
          "Search may return insufficient results for provider '{}' (supported: {}, requested: {})",
          searchUrl,
          box(maxResultsPerPage),
          box(maxResults));
    }

    try {
      Document doc =
          connection()
              .data(queryParam, URLEncoder.encode(query, StandardCharsets.UTF_8))
              .data(resultCountParam, String.valueOf(maxResults))
              .get();
      LOG.info(
          "Web search for query '{}' (max. results: {}) took {} ms",
          query,
          box(maxResults),
          box(System.currentTimeMillis() - startTime));
      return doc;
    } catch (IOException e) {
      throw new SearchException(
          "An error occurred while trying to execute a web search for query string '" + query + "'",
          e);
    }
  }

  private Connection connection() {
    if (connection == null) {
      connection = Jsoup.connect(searchUrl).userAgent(userAgent);
    }
    return connection;
  }
}
