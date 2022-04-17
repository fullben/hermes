package de.fullben.hermes.search;

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
      implements FirstStep, QueryStep, ResultCountStep, UserAgentStep, FinalStep {

    private String searchUrl;
    private String queryParam;
    private String resultCountParam;
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
    public UserAgentStep resultCountParam(String resultCountParam) {
      this.resultCountParam = notBlank(resultCountParam);
      return this;
    }

    @Override
    public FinalStep defaultUserAgent() {
      this.userAgent = "ExampleBot 2.0 (+http://example.com/bot)";
      return this;
    }

    @Override
    public FinalStep userAgent(String userAgent) {
      this.userAgent = notBlank(userAgent);
      return this;
    }

    @Override
    public WebSearchClient build() {
      return new WebSearchClient(searchUrl, queryParam, resultCountParam, userAgent);
    }
  }

  public interface FirstStep {

    /**
     * Sets the address of the web search to be used by the client (e.g., {@code
     * http://www.google.com/search} for the Google web search).
     *
     * @param url the web address of a web search
     * @return this builder instance
     */
    QueryStep searchUrl(String url);
  }

  public interface QueryStep {

    /**
     * Sets the name of the URL parameter used by the web search provider to set the query term.
     *
     * @param queryParam the name of the query URL parameter
     * @return this builder instance
     */
    ResultCountStep queryParam(String queryParam);
  }

  public interface ResultCountStep {

    /**
     * Sets the name of the URL parameter use by the web search provider to indicate the number of
     * search results.
     *
     * @param resultCountParam the name of the URL parameter indicating the search result count
     * @return this builder instance
     */
    UserAgentStep resultCountParam(String resultCountParam);
  }

  public interface UserAgentStep {

    /**
     * Sets some arbitrary (meaningless) value for the user agent executing the web search requests.
     *
     * @return this builder instance
     */
    FinalStep defaultUserAgent();

    /**
     * Sets the given value as the user agent executing the web search requests.
     *
     * @param userAgent the user agent
     * @return this builder instance
     */
    FinalStep userAgent(String userAgent);
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
