package de.fullben.hermes.search;

import de.fullben.hermes.api.InvalidParamException;
import de.fullben.hermes.representation.SearchResultRepresentation;
import de.fullben.hermes.search.bing.BingSearchService;
import de.fullben.hermes.search.google.GoogleSearchService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for running web searches against various web search implementations.
 *
 * @author Benedikt Full
 */
@Service
public class WebSearchService {

  private final GoogleSearchService googleSearchService;
  private final BingSearchService bingSearchService;

  @Autowired
  public WebSearchService(
      GoogleSearchService googleSearchService, BingSearchService bingSearchService) {
    this.googleSearchService = googleSearchService;
    this.bingSearchService = bingSearchService;
  }

  /**
   * Runs a web search based on the given parameters and returns the parsed results.
   *
   * @param query the query string, usually case-insensitive
   * @param resultCount the number of results to be returned
   * @param provider the web search provider to be employed, e.g., Google
   * @return the found results
   * @throws SearchException if an error occurs while executing the web search or processing its
   *     result data
   * @throws InvalidParamException if the given {@code provider} can not be matched with any of the
   *     supported providers
   */
  public List<SearchResultRepresentation> search(String query, int resultCount, String provider)
      throws SearchException {
    SearchProvider searchProvider = SearchProvider.find(provider);
    if (searchProvider == SearchProvider.GOOGLE) {
      return googleSearchService.search(query, resultCount);
    } else if (searchProvider == SearchProvider.BING) {
      return bingSearchService.search(query, resultCount);
    } else {
      throw new InvalidParamException("Unsupported search provider: " + provider);
    }
  }
}
