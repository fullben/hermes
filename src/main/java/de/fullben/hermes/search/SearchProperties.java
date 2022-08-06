package de.fullben.hermes.search;

import javax.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration for the search implementations.
 *
 * @author Benedikt Full
 */
@ConfigurationProperties(prefix = "hermes.search")
@ConstructorBinding
@Validated
public class SearchProperties {

  @Min(1)
  private final int cacheExpireAfterMins;

  @Min(1)
  private final int cacheMaxSize;

  @Min(1)
  private final int maxTries;

  public SearchProperties(int cacheExpireAfterMins, int cacheMaxSize, int maxTries) {
    this.cacheExpireAfterMins = cacheExpireAfterMins;
    this.cacheMaxSize = cacheMaxSize;
    this.maxTries = maxTries;
  }

  public int getCacheExpireAfterMins() {
    return cacheExpireAfterMins;
  }

  public int getCacheMaxSize() {
    return cacheMaxSize;
  }

  public int getMaxTries() {
    return maxTries;
  }
}
