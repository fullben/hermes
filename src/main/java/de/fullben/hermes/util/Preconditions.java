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

  /**
   * Returns the given object if it is not {@code null}. If {@code null} is provided, an {@link
   * IllegalArgumentException} is thrown.
   *
   * @param t some object, may be {@code null}
   * @param <T> the type of the given object
   * @return the given object
   * @throws IllegalArgumentException if the given object is {@code null}
   */
  public static <T> T notNull(T t) {
    if (t == null) {
      throw new IllegalArgumentException("Must not be null");
    }
    return t;
  }

  /**
   * Returns the given string if it is neither {@code null} nor blank (as defined by the {@link
   * String#isBlank() isBlank()} method. Otherwise, an {@link IllegalArgumentException} is thrown.
   *
   * @param s some string or {@code null}
   * @return the given string
   * @throws IllegalArgumentException if the given string is {@code null} or blank
   */
  public static String notBlank(String s) {
    if (s == null || s.isBlank()) {
      throw new IllegalArgumentException("Must be neither null nor blank");
    }
    return s;
  }

  /**
   * Checks whether the given value {@code i} is greater than the provided boundary value. If this
   * is the case, the value of {@code i} is returned. Otherwise, an {@link IllegalArgumentException}
   * will be thrown.
   *
   * @param bound some boundary value
   * @param i some value
   * @return the value of the {@code i} parameter
   * @throws IllegalArgumentException if the value of {@code i} is not greather than the value of
   *     the provided {@code bound}
   */
  public static int greaterThan(int bound, int i) {
    if (i <= bound) {
      throw new IllegalArgumentException("Value must be greater than " + bound);
    }
    return i;
  }
}
