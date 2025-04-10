package com.management.library.controller;

import com.management.library.dto.LoanDTO;
import com.management.library.dto.LoanRequestDTO;
import com.management.library.exception.BookNotAvailableException;
import com.management.library.exception.LoanLimitExceededException;
import com.management.library.exception.LoanNotFoundException;
import com.management.library.exception.StudentNotFoundException;
import com.management.library.service.LoanService;
import com.management.library.service.StudentServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;
    private final StudentServiceClient studentServiceClient;

    @PostMapping("/checkout")
    @ResponseStatus(HttpStatus.CREATED)
    public LoanDTO checkOutBook(@RequestBody LoanRequestDTO requestDTO, @AuthenticationPrincipal User user){
        boolean studentExists = studentServiceClient.doesStudentExist(requestDTO.getStudentId());
        if (!studentExists) {
            throw new StudentNotFoundException("Student not found with ID : "+ requestDTO.getStudentId());
        }
        return loanService.checkOutBook(requestDTO, user.getUsername());
    }

    @PutMapping("/return/{loanId}")
    public LoanDTO returnBook(@PathVariable Long loanId, @AuthenticationPrincipal User user){
        return loanService.returnBook(loanId, user.getUsername());
    }

    @GetMapping("/active/student/{studentId}")
    public List<LoanDTO> getActiveLoans(@PathVariable Long studentId){
        return loanService.getActiveLoansByStudent(studentId);
    }

    @GetMapping("/active/me")
    public List<LoanDTO> getMyActiveLoans(@AuthenticationPrincipal User user){
        Long studentId = Long.parseLong(user.getUsername());
        return loanService.getActiveLoansByStudent(studentId);
    }

    @ExceptionHandler({
            BookNotAvailableException.class,
            LoanLimitExceededException.class,
            LoanNotFoundException.class
    })
    public ResponseEntity<String> handleExceptions(Exception ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}