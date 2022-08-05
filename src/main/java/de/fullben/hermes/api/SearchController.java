package de.fullben.hermes.api;

import de.fullben.hermes.representation.ErrorRepresentation;
import de.fullben.hermes.representation.SearchResultRepresentation;
import de.fullben.hermes.search.SearchException;
import de.fullben.hermes.search.SearchProvider;
import de.fullben.hermes.search.WebSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import javax.validation.constraints.Min;
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
 * Controller for all endpoints related to web search activities.
 *
 * @author Benedikt Full
 */
@RestController
@RequestMapping("/api")
@Validated
public class SearchController {

  private final WebSearchService webSearchService;

  @Autowired
  public SearchController(WebSearchService webSearchService) {
    this.webSearchService = webSearchService;
  }

  @Operation(
      summary = "Returns web search results",
      description =
          "Can be used to acquire a specific number of search results from a given web search provider.",
      parameters = {
        @Parameter(name = "q", description = "The query string, case-insensitive", required = true),
        @Parameter(name = "n", description = "The number of results to be returned"),
        @Parameter(
            name = "p",
            description =
                "The web search provider to be used for the search, supported are Google and Bing")
      },
      responses = {
        @ApiResponse(
            responseCode = "200",
            description =
                "If the application was able to successfully use the provided query for executing a web search"),
        @ApiResponse(
            responseCode = "400",
            description =
                "If the given query string is null or blank, the result count is smaller than one, or the given provider is invalid",
            content = {@Content(schema = @Schema(implementation = ErrorRepresentation.class))}),
        @ApiResponse(
            responseCode = "500",
            description =
                "If an error arises while executing the web search or processing its results",
            content = {@Content(schema = @Schema(implementation = ErrorRepresentation.class))})
      })
  @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<SearchResultRepresentation>> search(
      @RequestParam("q") @NotBlank String query,
      @RequestParam(value = "n", required = false, defaultValue = "10") @Min(1) int resultCount,
      @RequestParam(value = "p", required = false, defaultValue = "GOOGLE") SearchProvider provider)
      throws SearchException {
    return ResponseEntity.ok(webSearchService.search(query, resultCount, provider));
  }
}
