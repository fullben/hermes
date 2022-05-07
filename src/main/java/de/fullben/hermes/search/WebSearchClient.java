package de.fullben.hermes.search;

import static de.fullben.hermes.util.Preconditions.greaterThan;
import static de.fullben.hermes.util.Preconditions.notBlank;
import static org.apache.logging.log4j.util.Unbox.box;

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
  private final String resultsPerPageParam;
  private final int maxResultsPerPage;
  private final String userAgent;
  private Connection connection;

  private WebSearchClient(
      String searchUrl,
      String queryParam,
      String resultsPerPageParam,
      int maxResultsPerPage,
      String userAgent) {
    this.searchUrl = notBlank(searchUrl);
    this.queryParam = notBlank(queryParam);
    this.resultsPerPageParam = notBlank(resultsPerPageParam);
    this.maxResultsPerPage = greaterThan(0, maxResultsPerPage);
    this.userAgent = notBlank(userAgent);
    connection = null;
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
              .data(resultsPerPageParam, String.valueOf(maxResults))
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

  /**
   * The {@link WebSearchClient} step builder implementation.
   *
   * @author Benedikt Full
   */
  public static class Builder
      implements FirstStep, QueryStep, ResultCountStep, ResultLimitStep, UserAgentStep, FinalStep {

    private String searchUrl;
    private String queryParam;
    private String resultsPerPageParam;
    private int maxResultsPerPage;
    private String userAgent;

    @Override
    public QueryStep searchUrl(String searchUrl) {
      this.searchUrl = notBlank(searchUrl);
      return this;
    }

    @Override
    public ResultCountStep queryParam(String queryParam) {
      this.queryParam = notBlank(queryParam);
      return this;
    }

    @Override
    public ResultLimitStep resultsPerPageParam(String resultsPerPageParam) {
      this.resultsPerPageParam = notBlank(resultsPerPageParam);
      return this;
    }

    @Override
    public UserAgentStep maxResultsPerPage(int maxResultsPerPage) {
      this.maxResultsPerPage = greaterThan(0, maxResultsPerPage);
      return this;
    }

    @Override
    public FinalStep userAgent(String userAgent) {
      this.userAgent = notBlank(userAgent);
      return this;
    }

    @Override
    public FinalStep defaultUserAgent() {
      return userAgent("ExampleBot 2.0 (+http://example.com/bot)");
    }

    @Override
    public FinalStep firefoxOnWindowsUserAgent() {
      return userAgent(
          "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:99.0) Gecko/20100101 Firefox/99.0");
    }

    @Override
    public WebSearchClient build() {
      return new WebSearchClient(
          searchUrl, queryParam, resultsPerPageParam, maxResultsPerPage, userAgent);
    }
  }

  public interface FirstStep {

    /**
     * The address of the web search to be used by the client (e.g., {@code
     * http://www.google.com/search} for the Google web search).
     *
     * @param url the web address of a web search
     * @return this builder instance
     */
    QueryStep searchUrl(String url);
  }

  public interface QueryStep {

    /**
     * The name of the URL parameter used by the web search provider to set the query term.
     *
     * @param queryParam the name of the query URL parameter
     * @return this builder instance
     */
    ResultCountStep queryParam(String queryParam);
  }

  public interface ResultCountStep {

    /**
     * The name of the URL parameter use by the web search provider to indicate the number of search
     * results per page.
     *
     * @param resultsPerPageParam the name of the URL parameter indicating the search result count
     * @return this builder instance
     */
    ResultLimitStep resultsPerPageParam(String resultsPerPageParam);
  }

  public interface ResultLimitStep {

    /**
     * The maximum number of results per page supported by the web search to the given value.
     *
     * @param maxResults the maximum number of results per page, a value greater than zero
     * @return this builder instance
     */
    UserAgentStep maxResultsPerPage(int maxResults);
  }

  public interface UserAgentStep {

    /**
     * The user agent executing the web search requests.
     *
     * @param userAgent the user agent
     * @return this builder instance
     */
    FinalStep userAgent(String userAgent);

    /**
     * Uses some arbitrary and meaningless value for the user agent executing the web search
     * requests.
     *
     * @return this builder instance
     */
    FinalStep defaultUserAgent();

    /**
     * Sets the user agent to a recent (04/2022) version of Mozilla Firefox running on a Windows 10
     * machine.
     *
     * @return this builder instance
     */
    FinalStep firefoxOnWindowsUserAgent();
  }

  public interface FinalStep {

    /**
     * Creates a {@code WebSearchClient} instance based on the state of this builder.
     *
     * @return a new {@code WebSearchClient} instance
     */
    WebSearchClient build();
  }
}
