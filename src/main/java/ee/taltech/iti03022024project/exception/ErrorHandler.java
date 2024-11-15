package ee.taltech.iti03022024project.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@ControllerAdvice
@Slf4j
public class ErrorHandler {

    // Not used in this sprint
//    // Data validation errors
//    @ExceptionHandler(ApplicationException.class)
//    public ResponseEntity<Object> handleApplicationException(ApplicationException ex) {
//        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.I_AM_A_TEAPOT);
//    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex) {
        log.error("Internal server error", ex);
        // Return ErrorResponse with message "Internal server error" and status code 500
        return new ResponseEntity<>(new ErrorResponse("Internal server error"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<Object> handleValidationException(HandlerMethodValidationException ex) {
        log.error("Validation error", ex);

        String message = ex.getAllValidationResults()
                .iterator().next().getResolvableErrors()
                .iterator().next().getDefaultMessage();
        // Return ErrorResponse with message "Validation error" and status code 400
        return new ResponseEntity<>(new ErrorResponse(message), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException ex) {
        log.error("Validation error", ex);

        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");
        // Return ErrorResponse with message "Validation error" and status code 400
        return new ResponseEntity<>(new ErrorResponse(message), HttpStatus.BAD_REQUEST);
    }
}
