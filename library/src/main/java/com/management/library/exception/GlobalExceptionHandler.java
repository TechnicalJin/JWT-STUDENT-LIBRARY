package com.management.library.exception;

import com.management.library.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BookNotAvailableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBookNotAvailable(BookNotAvailableException ex, WebRequest request) {
        return ErrorResponse.of(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getDescription(false));
    }

    @ExceptionHandler({ReservationNotFoundException.class, StudentNotFoundException.class, LoanNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundExceptions(RuntimeException ex, WebRequest request) {
        return ErrorResponse.of(HttpStatus.NOT_FOUND, ex.getMessage(), request.getDescription(false));
    }

    @ExceptionHandler(LoanLimitExceededException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)  // Changed from BANDWIDTH_LIMIT_EXCEEDED to TOO_MANY_REQUESTS
    public ErrorResponse handleLoanLimitExceeded(LoanLimitExceededException ex, WebRequest request) {
        return ErrorResponse.of(HttpStatus.TOO_MANY_REQUESTS, ex.getMessage(), request.getDescription(false));
    }

    @ExceptionHandler(InvalidReservationStatusException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidReservationStatus(InvalidReservationStatusException ex, WebRequest request) {
        return ErrorResponse.of(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getDescription(false));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleAllExceptions(Exception ex, WebRequest request) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        return ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", request.getDescription(false));
    }

    @ExceptionHandler(StudentServiceException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public ErrorResponse handleStudentServiceException(StudentServiceException ex, WebRequest request) {
        return ErrorResponse.of(HttpStatus.BAD_GATEWAY, ex.getMessage(), request.getDescription(false));
    }
}