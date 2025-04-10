package com.management.library.exception;

public class InvalidReservationStatusException extends RuntimeException {
    public InvalidReservationStatusException(String message, String action) {
        super(message);
    }
}
