package de.fullben.hermes.api;

import de.fullben.hermes.representation.ErrorRepresentation;
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
 * Defines handling for some exception types expected to be encountered at the web API level.
 *
 * @author Benedikt Full
 */
@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

  private static final Logger LOG = LogManager.getLogger(ApiExceptionHandler.class);

  @ExceptionHandler(value = {ConstraintViolationException.class})
  protected ResponseEntity<Object> handleConflict(
      ConstraintViolationException ex, WebRequest request) {
    HttpStatus status = HttpStatus.BAD_REQUEST;
    return handleExceptionInternal(
        ex, new ErrorRepresentation(status, ex.getMessage()), new HttpHeaders(), status, request);
  }

  @ExceptionHandler(value = {SearchException.class})
  protected ResponseEntity<Object> handleConflict(SearchException ex, WebRequest request) {
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    String msg = "Something went wrong while trying to execute your search";
    log(ex, status, msg);
    return handleExceptionInternal(
        ex, new ErrorRepresentation(status, msg), new HttpHeaders(), status, request);
  }

  private void log(Exception e, HttpStatus status, String message) {
    LOG.warn("{}: {} (Responding with: {})", e.getClass().getSimpleName(), message, status);
  }
}
