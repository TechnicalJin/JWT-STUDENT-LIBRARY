package com.management.library.service;

import com.management.library.dto.LoanDTO;
import com.management.library.dto.LoanRequestDTO;
import com.management.library.exception.*;
import com.management.library.mapper.LoanMapper;
import com.management.library.model.Book;
import com.management.library.model.Loan;
import com.management.library.model.enums.BookStatus;
import com.management.library.model.enums.LoanStatus;
//import com.management.library.model.enums.NotificationType;
import com.management.library.repository.LoanRepository;
import com.management.library.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanService {

    private static final Logger logger = LoggerFactory.getLogger(LoanService.class);
    private static final int LOAN_PERIOD_DAYS = 14;

    private final LoanRepository loanRepository;
    private final BookService bookService;
    private final ReservationService reservationService;
    private final LoanMapper loanMapper;
    private final StudentServiceClient studentServiceClient;
//    private final NotificationService notificationService;

    @Autowired
    private RestTemplate restTemplate;

    @Transactional
    public LoanDTO checkOutBook(LoanRequestDTO requestDto, String librarianUsername){
        logger.info("Attempting to checkout book {} for student {}", requestDto.getBookId(), requestDto.getStudentId());

        boolean studentExists = studentServiceClient.doesStudentExist(requestDto.getStudentId());
        if (!studentExists) {
            throw new StudentNotFoundException("Student not found with ID : " + requestDto.getStudentId());
        }
        Book book = bookService.getBookEntity(requestDto.getBookId());
        validateCheckoutEligibility(book, requestDto.getStudentId());

        Loan loan = new Loan();
        loan.setBookId(requestDto.getBookId());
        loan.setStudentId(requestDto.getStudentId());
        loan.setCheckOutDate(LocalDateTime.now());
        loan.setDueDate(LocalDateTime.now().plus(LOAN_PERIOD_DAYS, ChronoUnit.DAYS));
        loan.setStatus(LoanStatus.ACTIVE);
        loan.setLibrarianCheckout(librarianUsername);

        updateBookForCheckout(book);
        handleReservation(requestDto);

        Loan savedLoan = loanRepository.save(loan);
        logger.info("Loan Created Successfully With ID: {}", savedLoan.getId());

        return loanMapper.loanToLoanDTO(savedLoan);
    }

    @Transactional
    public LoanDTO returnBook(Long loanId, String librarianUsername){
        logger.info("Processing return for loan ID: {}", loanId);

        Loan loan = loanRepository.findById(loanId).orElseThrow(()-> new LoanNotFoundException("Loan not found with Id: " + loanId));

        if (loan.getStatus() == LoanStatus.RETURNED){
            logger.warn("Attempted to return already returned loan ID: {}", loanId);
            throw new IllegalArgumentException("Book Already Returned");
        }

        loan.setReturnDate(LocalDateTime.now());
        loan.setStatus(LoanStatus.RETURNED);
        loan.setLibrarianCheckin(librarianUsername);

        Book book = bookService.getBookEntity(loan.getBookId());
        book.setAvailableCopies(book.getAvailableCopies() + 1);

        if (book.getAvailableCopies() > 0){
            book.setStatus(BookStatus.AVAILABLE);
        }

        bookService.updateBook(book);
        Loan updateLoan = loanRepository.save(loan);
        logger.info("Loan ID {} marked as returned", loanId);

        return loanMapper.loanToLoanDTO(updateLoan);
    }

    private void validateCheckoutEligibility(Book book, Long studentId){
        if (book.getAvailableCopies() < 1){
            logger.error("No available copies for book ID: {}", book.getId());
            throw new BookNotAvailableException("No available copies for this book",book.getId());
        }

        long activeLoans = loanRepository.countByStudentIdAndStatus(studentId, LoanStatus.ACTIVE);
        if (activeLoans >= 5){
            logger.warn("Student {} has reached maximum active loans", studentId);
            throw new LoanLimitExceededException("Maximum of 5 active loans allowed");
        }
    }

    private void updateBookForCheckout(Book book){
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        if (book.getAvailableCopies() == 0){
            book.setStatus(BookStatus.LOANED);
        }
        bookService.updateBook(book);
    }

    private void handleReservation(LoanRequestDTO requestDTO){
        try {
            reservationService.cancelReservationForCheckout(requestDTO.getBookId(), requestDTO.getStudentId());
        } catch (ReservationNotFoundException e) {
            logger.debug("No reservation found for book {} and student {}",requestDTO.getBookId(), requestDTO.getStudentId());
        }
    }

    @Scheduled(cron = "0 0 9 * * ?")// 9 AM
    @Transactional
    public void checkOverdueLoans(){
        logger.info("Starting overdue loans check");
        LocalDateTime now = LocalDateTime.now();

        List<Loan> overdueLoans = loanRepository.findByStatusAndDueDateBefore(LoanStatus.ACTIVE, now);

        logger.info("Found {} overdue loans", overdueLoans.size());
        overdueLoans.forEach(loan -> {
            loan.setStatus(LoanStatus.OVERDUE);
            loanRepository.save(loan);

            String message = String.format("Loan for Book ID %s is overdue. Please return it immediately.", loan.getBookId());
//            notificationService.createNotification(loan.getStudentId(), String.format("Loan for book '%s' (ID: %d) is overdue.",bookService.getBookEntity(loan.getBookId())), NotificationType.OVERDUE);
        });

        logger.info("Complete overdue loan processing");
    }


    public List<LoanDTO> getActiveLoansByStudent(Long studentId) {
        logger.info("Fetching active loans for student ID: {}", studentId);

        List<Loan> activeLoans = loanRepository.findByStudentIdAndStatus(studentId, LoanStatus.ACTIVE);

        logger.debug("Found {} active loans for student {}", activeLoans.size(), studentId);
        return activeLoans.stream()
                .map(loan->{
                    LoanDTO dto = loanMapper.loanToLoanDTO(loan);
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
