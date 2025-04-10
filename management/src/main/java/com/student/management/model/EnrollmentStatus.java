package com.student.management.model;

public enum EnrollmentStatus {
    ACTIVE,         // Currently enrolled and attending classes
    INACTIVE,       // Not currently attending (may return)
    GRADUATED,      // Successfully completed program
    SUSPENDED,      // Temporarily barred from attending
    WITHDRAWN,      // Permanently left the institution
    PROBATION,      // Academic or disciplinary probation
    DEFERRED,       // Admission deferred to a later term
    LEAVE_OF_ABSENCE // Official temporary leave
}