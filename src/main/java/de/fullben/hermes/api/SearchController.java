package de.fullben.hermes.api;

import de.fullben.hermes.representation.SearchResultRepresentation;
import de.fullben.hermes.search.SearchException;
import de.fullben.hermes.search.google.GoogleSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import javax.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for all endpoints related to (Google) web search activities.
 *
 * @author Benedikt Full
 */
@RestController
@RequestMapping("/api")
@Validated
public class SearchController {

  private final GoogleSearchService googleSearchService;

  @Autowired
  public SearchController(GoogleSearchService googleSearchService) {
    this.googleSearchService = googleSearchService;
  }

  @Operation(
      summary = "Returns Google search results",
      description = "Returns the first ten results from a Google search with the given query.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description =
                "If the application was able to successfully use the provided query for executing a Google search"),
        @ApiResponse(
            responseCode = "400",
            description = "If the given query string is null or blank"),
        @ApiResponse(
            responseCode = "500",
            description =
                "If an error arises while executing the Google search or processing its results")
      })
  @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<SearchResultRepresentation>> searchGoogle(
      @RequestParam("query") @NotBlank String query) throws SearchException {
    return ResponseEntity.ok(googleSearchService.search(query));
  }
}
