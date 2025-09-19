package com.management.library.exception;

public class ReservationRequiredException extends RuntimeException {
    public ReservationRequiredException(String message) {
        super(message);
    }

    public ReservationRequiredException(String message, Throwable cause) {
        super(message, cause);
    }
}