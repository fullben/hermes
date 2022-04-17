package de.fullben.hermes.search;

import java.util.Locale;

/**
 * The web search providers supported by the application.
 *
 * @author Benedikt Full
 */
public enum SearchProvider {
  /** Google web search. */
  GOOGLE,
  /** Bing web search. */
  BING;

  /**
   * Finds and returns the provider identified by the given string. The identifier is
   * case-insensitive.
   *
   * @param provider the name of the provider, may be {@code null}
   * @return the matching provider or {@code null} if no matching provider was found
   */
  public static SearchProvider find(String provider) {
    if (provider == null || provider.isBlank()) {
      return null;
    }
    provider = provider.toLowerCase(Locale.ROOT);
    for (SearchProvider p : values()) {
      if (p.toString().toLowerCase(Locale.ROOT).equals(provider)) {
        return p;
      }
    }
    return null;
  }
}
