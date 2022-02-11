package de.fullben.hermes.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.function.Supplier;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Unit tests for the parameter validation routines defined in the {@link Preconditions} class.
 *
 * @author Benedikt Full
 */
public class PreconditionsTests {

  @Test
  public void notNullThrowsIllegalArgumentExceptionIfParamIsNull() {
    assertThrows(IllegalArgumentException.class, () -> Preconditions.notNull(null));
  }

  @ParameterizedTest
  @MethodSource("nonNullObjects")
  public void notNullReturnsNonNullParam(Object o) {
    Supplier<Object> nullCheck = () -> Preconditions.notNull(o);
    Object checked = nullCheck.get();
    assertEquals(o, checked);
  }

  @ParameterizedTest
  @MethodSource("blankAndNullStrings")
  public void notBlankThrowsIllegalArgumentExceptionIfParamIsInvalid(String s) {
    assertThrows(IllegalArgumentException.class, () -> Preconditions.notBlank(s));
  }

  @ParameterizedTest
  @MethodSource("validStrings")
  public void notBlankReturnsNonBlankString(String s) {
    Supplier<String> nullCheck = () -> Preconditions.notBlank(s);
    String checked = nullCheck.get();
    assertEquals(s, checked);
  }

  @ParameterizedTest
  @MethodSource("invalidGreaterThanValues")
  public void greaterThanThrowsIllegalArgumentExceptionIfValueEqualToOrLessThanBound(
      int bound, int value) {
    assertThrows(IllegalArgumentException.class, () -> Preconditions.greaterThan(bound, value));
  }

  @ParameterizedTest
  @MethodSource("validGreaterThanValues")
  public void greaterThanReturnsValueIfGreaterThanBound(int bound, int value) {
    Supplier<Integer> valueSupplier = () -> Preconditions.greaterThan(bound, value);
    int i = valueSupplier.get();
    assertEquals(value, i);
  }

  protected static Stream<String> blankAndNullStrings() {
    return Stream.of("", "\t", " ", "    ", null);
  }

  protected static Stream<String> validStrings() {
    return Stream.of("zulu", "5", "\t yankee");
  }

  protected static Stream<Object> nonNullObjects() {
    return Stream.of("", 5, 1.55555, 1.5f);
  }

  protected static Stream<Arguments> invalidGreaterThanValues() {
    return Stream.of(
        Arguments.of(1, 1),
        Arguments.of(0, -1),
        Arguments.of(0, 0),
        Arguments.of(-1, -1),
        Arguments.of(-1, -2));
  }

  protected static Stream<Arguments> validGreaterThanValues() {
    return Stream.of(
        Arguments.of(1, 2),
        Arguments.of(0, 1),
        Arguments.of(0, 1),
        Arguments.of(-1, 0),
        Arguments.of(-1, 0));
  }
}
