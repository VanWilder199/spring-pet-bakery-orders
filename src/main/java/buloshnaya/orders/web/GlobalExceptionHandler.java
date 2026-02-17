package buloshnaya.orders.web;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(Exception e) {
        logger.error("An unexpected error occurred", e);

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                e.getMessage(),
                "INTERNAL_SERVER_ERROR",
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponseDto);
    }

    public ResponseEntity<ErrorResponseDto> handleEntityNotFound (EntityNotFoundException e) {
        logger.error("Entity not found", e);

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                e.getMessage(),
                "NOT_FOUND",
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorResponseDto);
    }

    @ExceptionHandler(value = {
            IllegalArgumentException.class,
            IllegalStateException.class,
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<ErrorResponseDto> handleBadRequest (Exception e) {
        logger.error("Bad request", e);

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                e.getMessage(),
                "BAD_REQUEST",
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponseDto);
    }
}
