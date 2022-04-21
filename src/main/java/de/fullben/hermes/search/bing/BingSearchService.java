package de.fullben.hermes.search.bing;

import de.fullben.hermes.search.CachingSearchService;
import de.fullben.hermes.search.SearchCacheConfiguration;
import de.fullben.hermes.search.WebSearchClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service that provides access to functionality centered around Bing's web search.
 *
 * @author Benedikt Full
 */
@Service
public class BingSearchService extends CachingSearchService {

  @Autowired
  public BingSearchService(SearchCacheConfiguration cacheConfig) {
    super(
        WebSearchClient.builder()
            .searchUrl("http://www.bing.com/search")
            .queryParam("q")
            .resultCountParam("count")
            .maxResultsPerPage(50)
            .defaultUserAgent()
            .build(),
        new BingSearchResultParser(),
        cacheConfig);
  }
}
