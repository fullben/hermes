package de.fullben.hermes.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configures a crude in-memory authentication source for the application by defining a set of user
 * credentials.
 *
 * @author Benedikt Full
 */
@Configuration
public class InMemoryAuthConfiguration {

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth, PasswordEncoder encoder)
      throws Exception {
    auth.inMemoryAuthentication()
        .withUser("user")
        .password(encoder.encode("user"))
        .roles(Roles.USER)
        .and()
        .withUser("admin")
        .password(encoder.encode("admin"))
        .roles(Roles.ADMIN);
  }
}
