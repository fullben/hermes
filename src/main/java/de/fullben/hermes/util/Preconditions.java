package de.fullben.hermes.util;

/**
 * Utility class for basic parameter validation.
 *
 * @author Benedikt Full
 */
public final class Preconditions {

  private Preconditions() {
    throw new AssertionError();
  }

  public static <T> T nonNull(T t) {
    if (t == null) {
      throw new IllegalArgumentException("Must not be null");
    }
    return t;
  }

  public static String nonBlank(String s) {
    if (s == null || s.isBlank()) {
      throw new IllegalArgumentException("Must be neither null nor blank");
    }
    return s;
  }

  public static int greaterThan(int bound, int i) {
    if (i <= bound) {
      throw new IllegalArgumentException("Value must be greater than " + bound);
    }
    return i;
  }
}
