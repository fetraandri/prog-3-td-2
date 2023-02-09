package app.foot.controller;

import app.foot.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class BaseController {

    @ExceptionHandler({BadRequestException.class})
    public void handleException() {
        log.info("A bad request exception was handled");
    }
    
      @ExceptionHandler(value = {MethodArgumentTypeMismatchException.class})
  ResponseEntity<RestException> handleConversionFailed(
      MethodArgumentTypeMismatchException e) {
    log.info("Conversion failed", e);
    String message = e.getCause().getCause().getMessage();
    return handleBadRequest(new BadRequestException(message));
  }
  @ExceptionHandler(value = {NotFoundException.class})
  ResponseEntity<RestException> handleNotFound(
      NotFoundException e) {
    log.info("Not found", e);
    return new ResponseEntity<>(toRest(e, HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
  }
     


}
