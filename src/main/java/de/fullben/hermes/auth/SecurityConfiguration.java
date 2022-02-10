package de.fullben.hermes.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Main security configuration of the application, defines which parts of the application are
 * accessible to authenticated users based on their roles. The application uses basic auth for
 * authenticating users.
 *
 * @author Benedikt Full
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  public SecurityConfiguration() {}

  @Override
  protected void configure(HttpSecurity security) throws Exception {
    security
        .csrf()
        .disable()
        .authorizeRequests()
        .antMatchers("/api/**")
        .hasRole(Roles.USER)
        .and()
        .authorizeRequests()
        .antMatchers("/swagger-ui/**")
        .hasRole(Roles.ADMIN)
        .and()
        .authorizeRequests()
        .antMatchers("/**")
        .permitAll()
        .anyRequest()
        .authenticated()
        .and()
        .httpBasic();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public RoleHierarchy roleHierarchy() {
    RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
    hierarchy.setHierarchy(Roles.prefixed(Roles.ADMIN) + " > " + Roles.prefixed(Roles.USER));
    return hierarchy;
  }
}
