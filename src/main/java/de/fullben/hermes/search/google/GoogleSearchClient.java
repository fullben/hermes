package de.fullben.hermes.search.google;

import static de.fullben.hermes.util.Preconditions.greaterThan;
import static de.fullben.hermes.util.Preconditions.nonBlank;
import static de.fullben.hermes.util.Preconditions.nonNull;
import static org.apache.logging.log4j.util.Unbox.box;

import de.fullben.hermes.search.SearchException;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Class for performing Google web searches, based on suggestions provided to a <a
 * href="https://stackoverflow.com/questions/3727662/how-can-you-search-google-programmatically-java-api">Stack
 * Overflow question regarding programmatic Google search usage</a>.
 *
 * @author Benedikt Full
 */
public class GoogleSearchClient {

  private static final Logger LOG = LogManager.getLogger(GoogleSearchClient.class);
  private final String userAgent;

  public GoogleSearchClient() {
    this("ExampleBot 2.0 (+http://example.com/bot)");
  }

  public GoogleSearchClient(String userAgent) {
    this.userAgent = nonNull(userAgent);
  }

  public Document search(String query, int maxResults) throws SearchException {
    long startTime = System.currentTimeMillis();
    try {
      Document doc =
          Jsoup.connect(googleSearchUrl(nonBlank(query), greaterThan(0, maxResults)))
              .userAgent(userAgent)
              .get();
      LOG.info(
          "Google search for query '{}' took {} ms",
          query,
          box(System.currentTimeMillis() - startTime));
      return doc;
    } catch (IOException e) {
      throw new SearchException(
          "An error occurred while trying to execute a Google web search for query string '"
              + query
              + "'",
          e);
    }
  }

  private String googleSearchUrl(String query, int maxResults) {
    return "http://www.google.com/search?q="
        + URLEncoder.encode(query, StandardCharsets.UTF_8)
        + "&num="
        + maxResults;
  }
}
