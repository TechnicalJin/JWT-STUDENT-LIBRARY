package com.management.library.exception;

public class LoanLimitExceededException extends RuntimeException{
    public LoanLimitExceededException(String message){
        super(message);
    }
}
