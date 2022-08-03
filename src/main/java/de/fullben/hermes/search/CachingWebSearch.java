package de.fullben.hermes.search;

import static de.fullben.hermes.util.Preconditions.greaterThan;
import static de.fullben.hermes.util.Preconditions.notBlank;
import static de.fullben.hermes.util.Preconditions.notNull;
import static org.apache.logging.log4j.util.Unbox.box;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import de.fullben.hermes.representation.SearchResultRepresentation;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;

/**
 * Base class for simple web search service implementations. Uses a specific web search
 * implementation which can be utilized to acquire a set of results for a given query term (see
 * {@link #search(String, int)}. Results are cached in an instance-maintained {@link Caffeine}
 * cache.
 *
 * @author Benedikt Full
 */
public abstract class CachingWebSearch {

  private static final Logger LOG = LogManager.getLogger(CachingWebSearch.class);
  private final WebSearchClient webSearchClient;
  private final SearchResultParser webSearchResultParser;
  private final Cache<String, List<SearchResultRepresentation>> resultCache;

  public CachingWebSearch(
      WebSearchClient webSearchClient,
      SearchResultParser webSearchResultParser,
      SearchCacheProperties cacheProperties) {
    this.webSearchClient = notNull(webSearchClient);
    this.webSearchResultParser = notNull(webSearchResultParser);
    notNull(cacheProperties);
    resultCache =
        Caffeine.newBuilder()
            .expireAfterWrite(cacheProperties.getExpireAfterMins(), TimeUnit.MINUTES)
            .maximumSize(cacheProperties.getMaxSize())
            .build();
  }

  /**
   * Runs the web search for the provided query term and returns a list of the found results.
   * Usually, these results are limited to the easily parsable results. In order to be <i>easily
   * parsable</i>, results must be in the default result formatting of the web search provider
   * employed by this instance. Web search providers will often present some of the first results in
   * a different layout than subsequent results. These results may not be returned by this method.
   *
   * <p>Note that this service employs caching. While the first call to this method with some
   * specific query (e.g., <i>apple</i>) will result in a request to the web search, subsequent
   * calls with the same query (e.g., <i>apple</i> or <i>Apple</i>, as web search queries are
   * case-insensitive) will return a cached result for a certain amount of time.
   *
   * @param query the search term, case-insensitive
   * @param resultCount the number of search results to be returned
   * @return a list of search results, containing the number of items specified via {@code
   *     resultCount}
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
    // Note: results will contain at least resultCount items
    List<SearchResultRepresentation> results = findResults(query, resultCount, 6);
    resultCache.put(query, results);
    // Results may contain more items than requested, thus limit
    return results.stream()
        .limit(resultCount)
        .map(SearchResultRepresentation::new)
        .collect(Collectors.toList());
  }

  private List<SearchResultRepresentation> findResults(String query, int resultCount, int maxTries)
      throws SearchException {
    // Use count+2 as initial value, not count, because usually, a page with n results will not
    // contain n parsable results
    int minResults = resultCount + 2;
    while ((maxTries -= 1) >= 0) {
      List<SearchResultRepresentation> results = searchAndParse(query, minResults);
      if (results.size() >= resultCount) {
        // If we have at least the requested amount of results, return ALL
        return results;
      }
      LOG.debug(
          "Insufficient results ({}) for query '{}' with min result {}, retrying",
          box(results.size()),
          query,
          box(minResults));
      minResults += 2;
    }
    throw new SearchException(
        "Failed to find " + resultCount + " results for query '" + query + "'");
  }

  private List<SearchResultRepresentation> searchAndParse(String query, int minResults)
      throws SearchException {
    List<Document> searchResults = webSearchClient.search(query, minResults);
    List<SearchResultRepresentation> parsedResults = new ArrayList<>();
    for (Document doc : searchResults) {
      parsedResults.addAll(webSearchResultParser.parse(doc));
    }
    return parsedResults;
  }
}
