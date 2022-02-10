package de.fullben.hermes.api;

import de.fullben.hermes.search.SearchException;
import javax.validation.ConstraintViolationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Defines handling for some exception types expected to be encountered at API level.
 *
 * @author Benedikt Full
 */
@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

  private static final Logger LOG = LogManager.getLogger(ApiExceptionHandler.class);

  @ExceptionHandler(value = {ConstraintViolationException.class})
  protected ResponseEntity<Object> handleConflict(
      ConstraintViolationException ex, WebRequest request) {
    return handleExceptionInternal(
        ex, ex.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(value = {SearchException.class})
  protected ResponseEntity<Object> handleConflict(SearchException ex, WebRequest request) {
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    log(ex, status);
    return handleExceptionInternal(
        ex,
        "Something went wrong while trying to execute your search",
        new HttpHeaders(),
        status,
        request);
  }

  private void log(Exception e, HttpStatus status) {
    LOG.warn("{}: {} (Responding with: {})", e.getClass().getSimpleName(), e.getMessage(), status);
  }
}
