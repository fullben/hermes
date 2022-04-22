package de.fullben.hermes.search;

import static de.fullben.hermes.util.Preconditions.greaterThan;
import static de.fullben.hermes.util.Preconditions.notBlank;

/**
 * This class hosts all major components that make up the step builder for constructing {@link
 * WebSearchClient} instances.
 *
 * @author Benedikt Full
 */
public class WebSearchClientBuilder {

  /**
   * The actual {@link WebSearchClient} step builder implementation.
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
