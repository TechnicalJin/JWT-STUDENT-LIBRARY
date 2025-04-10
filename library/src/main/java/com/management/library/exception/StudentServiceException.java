package com.management.library.exception;

public class StudentServiceException extends RuntimeException {
    public StudentServiceException(String errorCommunicatingWithStudentService) {
        super(errorCommunicatingWithStudentService);
    }
}
