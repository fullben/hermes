package de.fullben.hermes.search.google;

import static de.fullben.hermes.util.Preconditions.notBlank;
import static org.apache.logging.log4j.util.Unbox.box;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import de.fullben.hermes.representation.SearchResultRepresentation;
import de.fullben.hermes.search.SearchCacheConfiguration;
import de.fullben.hermes.search.SearchException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service that provides access to functionality centered around Google's web search.
 *
 * @author Benedikt Full
 */
@Service
public class GoogleSearchService {

  private static final Logger LOG = LogManager.getLogger(GoogleSearchClient.class);
  private final Cache<String, List<SearchResultRepresentation>> resultCache;
  private final GoogleSearchClient googleSearchClient;

  @Autowired
  public GoogleSearchService(SearchCacheConfiguration cacheConfig) {
    resultCache =
        Caffeine.newBuilder()
            .expireAfterWrite(cacheConfig.getExpireAfterMins(), TimeUnit.MINUTES)
            .maximumSize(cacheConfig.getMaxSize())
            .build();
    googleSearchClient = new GoogleSearchClient();
  }

  /**
   * Runs the Google web search for the provided query term and returns a list of the ten first
   * parsable results. In order to be <i>parsable</i>, results must be in default formatting. Google
   * will often present some of the first results in a different layout than subsequent results.
   * These results may not be returned by this method.
   *
   * <p>Note that this service employs caching. While the first call to this method with some
   * specific query (e.g., <i>apple</i>) will result in a request to the Google search, subsequent
   * calls with the same query (e.g., <i>apple</i> or <i>Apple</i>, as Google search queries are
   * case-insensitive) will return a cached result for a certain amount of time.
   *
   * @param query the search term
   * @return a list of ten search results
   * @throws SearchException if an error is encountered while trying to run the Google search or
   *     parsing the resulting website
   * @throws IllegalArgumentException if the given query is {@code null} or blank
   */
  public List<SearchResultRepresentation> search(String query) throws SearchException {
    // To lower, because Google search is not case-sensitive
    query = notBlank(query).toLowerCase(Locale.ROOT);

    // If cached search results exist, return a copy of the cached values
    List<SearchResultRepresentation> cachedResults = resultCache.getIfPresent(query);
    if (cachedResults != null) {
      return cachedResults.stream()
          .map(SearchResultRepresentation::new)
          .collect(Collectors.toList());
    }

    // Actually execute the Google search, parse, cache, and return the result
    List<SearchResultRepresentation> results = findTenResults(query, 6);
    resultCache.put(query, results);
    return results;
  }

  private List<SearchResultRepresentation> findTenResults(String query, int maxTries)
      throws SearchException {
    // Use 12 as initial value, not 10, because usually, a page with 10 results will not contain 10
    // parsable results
    int maxResults = 12;
    while ((maxTries -= 1) >= 0) {
      List<SearchResultRepresentation> results = search(query, maxResults);
      if (results.size() >= 10) {
        return results.size() > 10 ? results.subList(0, 10) : results;
      }
      LOG.debug(
          "Insufficient results ({}) for query '{}' with max result {}, retrying",
          box(results.size()),
          query,
          box(maxResults));
      maxResults += 2;
    }
    throw new SearchException("Failed to find ten results for query '" + query + "'");
  }

  private List<SearchResultRepresentation> search(String query, int maxResults)
      throws SearchException {
    Document searchResults = googleSearchClient.search(query, maxResults);
    return new GoogleSearchResultParser().parse(searchResults);
  }
}
