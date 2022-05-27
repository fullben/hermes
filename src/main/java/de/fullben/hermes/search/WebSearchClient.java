package de.fullben.hermes.search;

import static de.fullben.hermes.util.Preconditions.greaterThan;
import static de.fullben.hermes.util.Preconditions.notBlank;
import static org.apache.logging.log4j.util.Unbox.box;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
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
  private final String pageStartParam;
  private final int pageStartBase;
  private final String userAgent;
  private Connection connection;

  private WebSearchClient(
      String searchUrl,
      String queryParam,
      String resultsPerPageParam,
      int maxResultsPerPage,
      String pageStartParam,
      int pageStartBase,
      String userAgent) {
    this.searchUrl = notBlank(searchUrl);
    this.queryParam = notBlank(queryParam);
    this.resultsPerPageParam = notBlank(resultsPerPageParam);
    this.maxResultsPerPage = greaterThan(0, maxResultsPerPage);
    this.pageStartParam = notBlank(pageStartParam);
    this.pageStartBase = greaterThan(-1, pageStartBase);
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
   * Uses the web search configured for this client to return a list of web documents containing the
   * result data for the provided query. Note that in total, the returned documents will contain at
   * least the number of (parsable) results as defined with the {@code minResults} parameter.
   *
   * @param query the search term, usually case-insensitive
   * @param minResults the minimum number of results to be acquired by the search
   * @return the web search result pages
   * @throws SearchException if an error occurs while executing the search
   */
  public List<Document> search(String query, int minResults) throws SearchException {
    long startTime = System.currentTimeMillis();
    notBlank(query);
    greaterThan(0, minResults);
    List<Document> results;

    if (minResults >= (maxResultsPerPage - 4)) {
      // Perform search with pagination
      int pageCount = (int) Math.ceil((double) minResults / maxResultsPerPage);
      results = new ArrayList<>(pageCount);
      int pageStart = pageStartBase;
      for (int i = 0; i < pageCount; i++) {
        try {
          results.add(
              connectionWithBasicQueryData(query, maxResultsPerPage)
                  .data(pageStartParam, String.valueOf(pageStart))
                  .get());
          // Increment page start index so that subsequent request fetches "next" page
          pageStart += maxResultsPerPage;
          LOG.debug(
              "Web search for query '{}' (page {}/{}) took {} ms",
              query,
              box(i + 1),
              box(pageCount),
              box(System.currentTimeMillis() - startTime));
        } catch (IOException e) {
          throw new SearchException(
              "An error occurred while trying to execute a web search for query string '"
                  + query
                  + "' (page "
                  + (i + 1)
                  + "/"
                  + pageCount
                  + ")",
              e);
        }
      }
      LOG.info(
          "Web search for query '{}' (pages: {}) took {} ms",
          query,
          box(pageCount),
          box(System.currentTimeMillis() - startTime));
    } else {
      // Perform single-page search
      results = new ArrayList<>(1);
      try {
        results.add(connectionWithBasicQueryData(query, minResults).get());
        LOG.info(
            "Web search for query '{}' (min results: {}) took {} ms",
            query,
            box(minResults),
            box(System.currentTimeMillis() - startTime));
      } catch (IOException e) {
        throw new SearchException(
            "An error occurred while trying to execute a web search for query string '"
                + query
                + "'",
            e);
      }
    }

    return results;
  }

  private Connection connectionWithBasicQueryData(String query, int resultCount) {
    if (connection == null) {
      connection = Jsoup.connect(searchUrl).userAgent(userAgent);
    }
    return connection
        .data(queryParam, URLEncoder.encode(query, StandardCharsets.UTF_8))
        .data(resultsPerPageParam, String.valueOf(resultCount));
  }

  /**
   * The {@link WebSearchClient} step builder implementation.
   *
   * @author Benedikt Full
   */
  public static class Builder
      implements FirstStep,
          QueryStep,
          PageStep,
          PageSizeStep,
          PaginationStep,
          PaginationBaseStep,
          UserAgentStep,
          FinalStep {

    private String searchUrl;
    private String queryParam;
    private String resultsPerPageParam;
    private int maxResultsPerPage;
    private String pageStartParam;
    private int pageStartBase;
    private String userAgent;

    @Override
    public QueryStep searchUrl(String searchUrl) {
      this.searchUrl = notBlank(searchUrl);
      return this;
    }

    @Override
    public PageStep queryParam(String queryParam) {
      this.queryParam = notBlank(queryParam);
      return this;
    }

    @Override
    public PageSizeStep resultsPerPageParam(String resultsPerPageParam) {
      this.resultsPerPageParam = notBlank(resultsPerPageParam);
      return this;
    }

    @Override
    public PaginationStep maxResultsPerPage(int maxResultsPerPage) {
      this.maxResultsPerPage = greaterThan(0, maxResultsPerPage);
      return this;
    }

    @Override
    public PaginationBaseStep pageStartParam(String pageStartParam) {
      this.pageStartParam = notBlank(pageStartParam);
      return this;
    }

    @Override
    public UserAgentStep pageStartBase(int base) {
      this.pageStartBase = greaterThan(-1, base);
      return this;
    }

    @Override
    public UserAgentStep zeroBasedPageStart() {
      return pageStartBase(0);
    }

    @Override
    public UserAgentStep oneBasedPageStart() {
      return pageStartBase(1);
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
          searchUrl,
          queryParam,
          resultsPerPageParam,
          maxResultsPerPage,
          pageStartParam,
          pageStartBase,
          userAgent);
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
    PageStep queryParam(String queryParam);
  }

  public interface PageStep {

    /**
     * The name of the URL parameter use by the web search provider to indicate the number of search
     * results per page.
     *
     * @param resultsPerPageParam the name of the URL parameter indicating the search result count
     * @return this builder instance
     */
    PageSizeStep resultsPerPageParam(String resultsPerPageParam);
  }

  public interface PageSizeStep {

    /**
     * The maximum number of results per page supported by the web search to the given value.
     *
     * @param maxResults the maximum number of results per page, a value greater than zero
     * @return this builder instance
     */
    PaginationStep maxResultsPerPage(int maxResults);
  }

  public interface PaginationStep {

    /**
     * The name of the URL parameter used by the web search provider to indicate which item out of
     * the entire result list found for a given query should be the first on some page.
     *
     * <p>While in other use cases, pagination is commonly handled by employing the page index and
     * the page size, many web search providers seem to rely on a different approach. They usually
     * use page size and index of the first item to be displayed on the page.
     *
     * @param pageStartParam the name of the URL parameter indicating the first item index
     * @return this builder instance
     */
    PaginationBaseStep pageStartParam(String pageStartParam);
  }

  public interface PaginationBaseStep {

    /**
     * The base (first value) of the indexing system used by the search provider to uniquely
     * identify each search result item.
     *
     * @param base the base value, usually zero or one
     * @return this builder instance
     */
    UserAgentStep pageStartBase(int base);

    /**
     * The base (first value) of the indexing system used by the search provider as zero.
     *
     * @return this builder instance
     * @see #pageStartBase(int)
     */
    UserAgentStep zeroBasedPageStart();

    /**
     * The base (first value) of the indexing system used by the search provider as one.
     *
     * @return this builder instance
     * @see #pageStartBase(int)
     */
    UserAgentStep oneBasedPageStart();
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
     * @see #userAgent(String)
     */
    FinalStep defaultUserAgent();

    /**
     * Sets the user agent to a recent (04/2022) version of Mozilla Firefox running on a Windows 10
     * machine.
     *
     * @return this builder instance
     * @see #userAgent(String)
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
