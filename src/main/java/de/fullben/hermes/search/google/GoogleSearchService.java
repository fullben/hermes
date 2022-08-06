package de.fullben.hermes.search.google;

import de.fullben.hermes.search.CachingWebSearch;
import de.fullben.hermes.search.SearchProperties;
import de.fullben.hermes.search.WebSearchClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

/**
 * Service that provides access to functionality centered around Google's web search.
 *
 * @author Benedikt Full
 */
@Service
@EnableConfigurationProperties(SearchProperties.class)
public class GoogleSearchService extends CachingWebSearch {

  @Autowired
  public GoogleSearchService(SearchProperties searchProperties) {
    super(
        WebSearchClient.builder()
            .searchUrl("http://www.google.com/search")
            .queryParam("q")
            .resultsPerPageParam("num")
            .maxResultsPerPage(100)
            .pageStartParam("start")
            .zeroBasedPageStart()
            .defaultUserAgent()
            .build(),
        new GoogleSearchResultParser(),
        searchProperties);
  }
}
