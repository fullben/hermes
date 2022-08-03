package de.fullben.hermes.search;

import javax.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration for the caches used by any search implementations.
 *
 * @author Benedikt Full
 */
@ConfigurationProperties(prefix = "hermes.search.cache")
@ConstructorBinding
@Validated
public class SearchCacheProperties {

  @Min(1)
  private final int expireAfterMins;

  @Min(1)
  private final int maxSize;

  public SearchCacheProperties(int expireAfterMins, int maxSize) {
    this.expireAfterMins = expireAfterMins;
    this.maxSize = maxSize;
  }

  public int getExpireAfterMins() {
    return expireAfterMins;
  }

  public int getMaxSize() {
    return maxSize;
  }
}
