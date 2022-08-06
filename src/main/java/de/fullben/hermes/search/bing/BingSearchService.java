package de.fullben.hermes.search.bing;

import de.fullben.hermes.search.CachingWebSearch;
import de.fullben.hermes.search.SearchProperties;
import de.fullben.hermes.search.WebSearchClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

/**
 * Service that provides access to functionality centered around Bing's web search.
 *
 * @author Benedikt Full
 */
@Service
@EnableConfigurationProperties(SearchProperties.class)
public class BingSearchService extends CachingWebSearch {

  @Autowired
  public BingSearchService(SearchProperties searchProperties) {
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
        searchProperties);
  }
}
