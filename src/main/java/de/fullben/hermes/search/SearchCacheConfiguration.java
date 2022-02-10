package de.fullben.hermes.search;

import javax.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration for the caches used by any search implementations.
 *
 * @author Benedikt Full
 */
@Configuration
@ConfigurationProperties(prefix = "hermes.search.cache")
@Validated
public class SearchCacheConfiguration {

  @Min(1)
  private int expireAfterMins;

  @Min(1)
  private int maxCacheSize;

  public SearchCacheConfiguration() {}

  public int getExpireAfterMins() {
    return expireAfterMins;
  }

  public void setExpireAfterMins(int expireAfterMins) {
    this.expireAfterMins = expireAfterMins;
  }

  public int getMaxCacheSize() {
    return maxCacheSize;
  }

  public void setMaxCacheSize(int maxCacheSize) {
    this.maxCacheSize = maxCacheSize;
  }
}
