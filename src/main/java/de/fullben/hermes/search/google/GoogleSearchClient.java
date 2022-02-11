package de.fullben.hermes.search.google;

import static de.fullben.hermes.util.Preconditions.greaterThan;
import static de.fullben.hermes.util.Preconditions.notBlank;
import static org.apache.logging.log4j.util.Unbox.box;

import de.fullben.hermes.search.SearchException;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Connection;
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
  private static final String GOOGLE_SEARCH_URL = "http://www.google.com/search";
  private static final String QUERY_PARAM = "q";
  private static final String RESULT_COUNT_PARAM = "num";
  private final String userAgent;
  private Connection connection;

  public GoogleSearchClient() {
    this("ExampleBot 2.0 (+http://example.com/bot)");
  }

  public GoogleSearchClient(String userAgent) {
    this.userAgent = notBlank(userAgent);
    connection = null;
  }

  public Document search(String query, int maxResults) throws SearchException {
    long startTime = System.currentTimeMillis();
    notBlank(query);
    greaterThan(0, maxResults);
    try {
      Document doc =
          connection()
              .data(QUERY_PARAM, URLEncoder.encode(query, StandardCharsets.UTF_8))
              .data(RESULT_COUNT_PARAM, String.valueOf(maxResults))
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

  private Connection connection() {
    if (connection == null) {
      connection = Jsoup.connect(GOOGLE_SEARCH_URL).userAgent(userAgent);
    }
    return connection;
  }
}
