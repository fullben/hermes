package de.fullben.hermes.search;

import static de.fullben.hermes.util.Preconditions.greaterThan;
import static de.fullben.hermes.util.Preconditions.notBlank;
import static de.fullben.hermes.util.Preconditions.notNull;
import static org.apache.logging.log4j.util.Unbox.box;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import de.fullben.hermes.representation.SearchResultRepresentation;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;

/**
 * Service capable of using a specific web search implementation to acquire a set of results for a
 * given query term. Results are cached.
 *
 * @author Benedikt Full
 */
public abstract class CachingSearchService {

  private static final Logger LOG = LogManager.getLogger(CachingSearchService.class);
  private final WebSearchClient webSearchClient;
  private final SearchResultParser webSearchResultParser;
  private final Cache<String, List<SearchResultRepresentation>> resultCache;

  public CachingSearchService(
      WebSearchClient webSearchClient,
      SearchResultParser webSearchResultParser,
      SearchCacheConfiguration cacheConfig) {
    this.webSearchClient = notNull(webSearchClient);
    this.webSearchResultParser = notNull(webSearchResultParser);
    notNull(cacheConfig);
    resultCache =
        Caffeine.newBuilder()
            .expireAfterWrite(cacheConfig.getExpireAfterMins(), TimeUnit.MINUTES)
            .maximumSize(cacheConfig.getMaxSize())
            .build();
  }

  /**
   * Runs the web search for the provided query term and returns a list of the first parsable
   * results. In order to be <i>parsable</i>, results must be in default formatting. Web search
   * providers will often present some of the first results in a different layout than subsequent
   * results. These results may not be returned by this method.
   *
   * <p>Note that this service employs caching. While the first call to this method with some
   * specific query (e.g., <i>apple</i>) will result in a request to the web search, subsequent
   * calls with the same query (e.g., <i>apple</i> or <i>Apple</i>, as web search queries are
   * case-insensitive) will return a cached result for a certain amount of time.
   *
   * @param query the search term
   * @param resultCount the number of search results to be returned
   * @return a list of search results
   * @throws SearchException if an error is encountered while trying to run the web search or
   *     parsing the resulting website
   * @throws IllegalArgumentException if the given query is {@code null} or blank, or the result
   *     count is smaller than 1
   */
  public List<SearchResultRepresentation> search(String query, int resultCount)
      throws SearchException {
    greaterThan(0, resultCount);
    // To lower, because usually web searches are not case-sensitive
    query = notBlank(query).toLowerCase(Locale.ROOT);

    // If cached search results exist, return a copy of the cached values
    List<SearchResultRepresentation> cachedResults = resultCache.getIfPresent(query);
    if (cachedResults != null && cachedResults.size() >= resultCount) {
      return cachedResults.stream()
          .limit(resultCount)
          .map(SearchResultRepresentation::new)
          .collect(Collectors.toList());
    }

    // Actually execute the web search, parse, cache, and return the result
    List<SearchResultRepresentation> results = findResults(query, resultCount, 6);
    resultCache.put(query, results);
    return results;
  }

  private List<SearchResultRepresentation> findResults(String query, int resultCount, int maxTries)
      throws SearchException {
    // Use count+2 as initial value, not count, because usually, a page with n results will not
    // contain n parsable results
    int maxResults = resultCount + 2;
    while ((maxTries -= 1) >= 0) {
      List<SearchResultRepresentation> results = searchAndParse(query, maxResults);
      if (results.size() >= resultCount) {
        return results.size() > resultCount ? results.subList(0, resultCount) : results;
      }
      LOG.debug(
          "Insufficient results ({}) for query '{}' with max result {}, retrying",
          box(results.size()),
          query,
          box(maxResults));
      maxResults += 2;
    }
    throw new SearchException(
        "Failed to find " + resultCount + " results for query '" + query + "'");
  }

  private List<SearchResultRepresentation> searchAndParse(String query, int maxResults)
      throws SearchException {
    Document searchResults = webSearchClient.search(query, maxResults);
    return webSearchResultParser.parse(searchResults);
  }
}
