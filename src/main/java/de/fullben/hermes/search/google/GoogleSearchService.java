package de.fullben.hermes.search.google;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import de.fullben.hermes.data.transfer.SearchResultRepresentation;
import de.fullben.hermes.search.SearchCacheConfiguration;
import de.fullben.hermes.search.SearchException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
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

  private final Cache<String, List<SearchResultRepresentation>> resultCache;

  @Autowired
  public GoogleSearchService(SearchCacheConfiguration cacheConfig) {
    resultCache =
        Caffeine.newBuilder()
            .expireAfterWrite(cacheConfig.getExpireAfterMins(), TimeUnit.MINUTES)
            .maximumSize(cacheConfig.getMaxCacheSize())
            .build();
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
    if (query == null || query.isBlank()) {
      throw new IllegalArgumentException("Query must be neither null nor blank");
    }
    // To lower, because Google search is not case-sensitive
    query = query.toLowerCase(Locale.ROOT);

    // If cached search results exist, return a copy of the cached values
    List<SearchResultRepresentation> cachedResults = resultCache.getIfPresent(query);
    if (cachedResults != null) {
      return cachedResults.stream()
          .map(SearchResultRepresentation::new)
          .collect(Collectors.toList());
    }

    // Actually execute the Google search, parse, cache, and return the result
    GoogleSearchClient searchClient = new GoogleSearchClient();
    Document searchResults = searchClient.search(query, 20);
    List<SearchResultRepresentation> results =
        new GoogleSearchResultParser()
            .parse(searchResults).stream().limit(10).collect(Collectors.toList());
    resultCache.put(query, results);
    return results;
  }
}
