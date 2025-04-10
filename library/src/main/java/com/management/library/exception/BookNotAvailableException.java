package com.management.library.exception;

public class BookNotAvailableException extends RuntimeException{
    public BookNotAvailableException(String message, Long id){
        super(message);
    }

    public BookNotAvailableException(String message) {
        super(message);
    }
}
