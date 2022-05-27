package de.fullben.hermes.search.bing;

import de.fullben.hermes.search.CachingWebSearch;
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
public class BingSearchService extends CachingWebSearch {

  @Autowired
  public BingSearchService(SearchCacheConfiguration cacheConfig) {
    super(
        WebSearchClient.builder()
            .searchUrl("http://www.bing.com/search")
            .queryParam("q")
            .resultsPerPageParam("count")
            .maxResultsPerPage(50)
            .pageStartParam("first")
            .oneBasedPageStart()
            .firefoxOnWindowsUserAgent()
            .build(),
        new BingSearchResultParser(),
        cacheConfig);
  }
}
