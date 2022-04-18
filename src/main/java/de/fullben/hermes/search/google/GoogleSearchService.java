package de.fullben.hermes.search.google;

import de.fullben.hermes.search.CachingSearchService;
import de.fullben.hermes.search.SearchCacheConfiguration;
import de.fullben.hermes.search.WebSearchClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service that provides access to functionality centered around Google's web search.
 *
 * @author Benedikt Full
 */
@Service
public class GoogleSearchService extends CachingSearchService {

  @Autowired
  public GoogleSearchService(SearchCacheConfiguration cacheConfig) {
    super(
        WebSearchClient.builder()
            .searchUrl("http://www.google.com/search")
            .queryParam("q")
            .resultCountParam("num")
            .maxResultsPerPage(100)
            .defaultUserAgent()
            .build(),
        new GoogleSearchResultParser(),
        cacheConfig);
  }
}
