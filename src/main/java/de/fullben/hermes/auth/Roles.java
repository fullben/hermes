package de.fullben.hermes.auth;

/**
 * The roles users of this application may have.
 *
 * @author Benedikt Full
 */
public final class Roles {

  public static final String ADMIN = "ADMIN";
  public static final String USER = "USER";
  private static final String ROLE_PREFIX = "ROLE_";

  private Roles() {
    throw new AssertionError();
  }

  public static String prefixed(String role) {
    if (role.startsWith(ROLE_PREFIX)) {
      return role;
    }
    return ROLE_PREFIX + role;
  }
}
