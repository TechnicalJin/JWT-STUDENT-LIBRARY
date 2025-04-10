package com.management.library.repository;

import com.management.library.model.Loan;
import com.management.library.model.enums.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByStudentIdAndStatus(Long studentId, LoanStatus status);
    List<Loan> findByBookIdAndStatus(Long bookId, LoanStatus status);
    long countByStudentIdAndStatus(Long studentId, LoanStatus status);
    List<Loan> findByStatusAndDueDateBefore(LoanStatus status, LocalDateTime now);
}
