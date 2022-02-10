package de.fullben.hermes.api;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Configuration for the Swagger UI API documentation provided by the application. By default, this
 * documentation should be available at {@code HOST:PORT/swagger-ui/index.html}.
 *
 * @author Benedikt Full
 */
@Configuration
@Profile("!prod")
public class SpringdocConfiguration {

  public SpringdocConfiguration() {}

  @Bean
  public OpenAPI springShopOpenAPI() {
    return new OpenAPI()
        .info(new Info().title("Hermes API").description("Bosch Coding Challenge Application"));
  }
}
