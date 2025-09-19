package com.management.library.service;

import com.management.library.dto.LoanDTO;
import com.management.library.dto.ReservationDTO;
import com.management.library.dto.ReservationRequestDTO;
import com.management.library.exception.InvalidReservationStatusException;
import com.management.library.exception.ReservationNotFoundException;
import com.management.library.exception.StudentNotFoundException;
import com.management.library.mapper.LoanMapper;
import com.management.library.mapper.ReservationMapper;
import com.management.library.model.Book;
import com.management.library.model.Loan;
import com.management.library.model.Reservation;
//import com.management.library.model.enums.NotificationType;
import com.management.library.model.enums.LoanStatus;
import com.management.library.model.enums.ReservationStatus;
import com.management.library.repository.LoanRepository;
import com.management.library.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private static final Logger logger = LoggerFactory.getLogger(ReservationService.class);
    private static final int LOAN_PERIOD_DAYS = 14;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private InterServiceClient interServiceClient;

    private final ReservationRepository reservationRepository;
    private final LoanRepository loanRepository;
    private final BookService bookService;
//    private final NotificationService notificationService;
    private final ReservationMapper reservationMapper;
    private final LoanMapper loanMapper;

    @Transactional
    public ReservationDTO createReservation(ReservationRequestDTO requestDTO) {
        logger.info("Creating reservation for student {} and book {}", requestDTO.getStudentId(), requestDTO.getBookId());
        
        // Validate student exists
        String url = "http://localhost:8081/api/students/exists/" + requestDTO.getStudentId();
        Boolean studentExists = interServiceClient.getForObject(url, Boolean.class);
        if (!Boolean.TRUE.equals(studentExists)) {
            throw new StudentNotFoundException("Student not found with ID : " + requestDTO.getStudentId());
        }

        // Validate book exists
        try {
            bookService.getBookById(requestDTO.getBookId());
        } catch (Exception e) {
            throw new RuntimeException("Book not found with ID: " + requestDTO.getBookId());
        }

        Reservation reservation = new Reservation();
        reservation.setBookId(requestDTO.getBookId());
        reservation.setStudentId(requestDTO.getStudentId());
        
        // Use the provided reservation date, converted to LocalDateTime
        if (requestDTO.getReservationDate() != null) {
            reservation.setReservationDate(requestDTO.getReservationDate().atStartOfDay());
        } else {
            reservation.setReservationDate(LocalDateTime.now());
        }
        
        reservation.setStatus(ReservationStatus.PENDING);

        Reservation savedReservation = reservationRepository.save(reservation);
        logger.info("Successfully created reservation with ID: {}", savedReservation.getId());
        
        return reservationMapper.reservationToReservationDTO(savedReservation);
    }

    @Transactional
    public void cancelReservationForCheckout(Long bookId, Long studentId){
        List<Reservation> reservations = reservationRepository.findByBookIdAndStudentIdAndStatus(bookId, studentId, ReservationStatus.APPROVED);

        if (!reservations.isEmpty()){
            reservations.forEach(reservation -> {
                reservation.setStatus(ReservationStatus.CANCELLED);
                reservationRepository.save(reservation);
                logger.info("Cancelled reservation ID: {} for checkout", reservation.getId());
            });
        }else {
            throw new ReservationNotFoundException("No approved reservation found");
        }
    }

    public boolean hasApprovedReservation(Long bookId, Long studentId){
        List<Reservation> reservations = reservationRepository.findByBookIdAndStudentIdAndStatus(bookId, studentId, ReservationStatus.APPROVED);
        return !reservations.isEmpty();
    }

    @Transactional
    public LoanDTO approveReservationAndCreateLoan(Long reservationId, String librarianUsername){
        logger.info("Approving reservation {} and creating loan by librarian {}", reservationId, librarianUsername);

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found"));

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new InvalidReservationStatusException("Only pending reservations can be approved", reservation.getStatus().toString());
        }

        // Approve the reservation
        reservation.setStatus(ReservationStatus.APPROVED);
        reservation.setProcessedDate(LocalDateTime.now());
        reservation.setProcessedBy(librarianUsername);
        reservationRepository.save(reservation);

        // Create the loan directly
        Loan loan = new Loan();
        loan.setBookId(reservation.getBookId());
        loan.setStudentId(reservation.getStudentId());
        loan.setCheckOutDate(LocalDateTime.now());
        loan.setDueDate(LocalDateTime.now().plus(LOAN_PERIOD_DAYS, ChronoUnit.DAYS));
        loan.setStatus(LoanStatus.ACTIVE);
        loan.setLibrarianCheckout(librarianUsername);

        // Update book availability
        Book book = bookService.getBookEntity(reservation.getBookId());
        if (book.getAvailableQuantity() > 0) {
            book.setAvailableQuantity(book.getAvailableQuantity() - 1);
            bookService.updateBook(book);
        }

        // Cancel the reservation now that loan is created
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);

        Loan savedLoan = loanRepository.save(loan);
        logger.info("Loan Created Successfully With ID: {} for approved reservation {}", savedLoan.getId(), reservationId);

        return loanMapper.loanToLoanDTO(savedLoan);
    }

    public List<ReservationDTO> getAllPendingReservations(){
        List<Reservation> pendingReservations = reservationRepository.findByStatus(ReservationStatus.PENDING);
        return pendingReservations.stream()
                .map(reservationMapper::reservationToReservationDTO)
                .toList();
    }

    @Transactional
    public ReservationDTO processReservation(Long reservationId, String action, String librarianUsername){
        logger.info("Processing reservation {} with action {} by librarian {}", reservationId, action, librarianUsername);

        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(()-> new ReservationNotFoundException("Reservation not found"));
        if (action.equalsIgnoreCase("APPROVE")){
            return approveReservation(reservation, librarianUsername);
        } else if (action.equalsIgnoreCase("REJECT")) {
            return rejectReservation(reservation, librarianUsername);
        }

        throw new InvalidReservationStatusException("Invalid action : ", action);
    }

    private ReservationDTO approveReservation(Reservation reservation, String librarianUsername){
        reservation.setStatus(ReservationStatus.APPROVED);
        reservation.setProcessedDate(LocalDateTime.now());
        reservation.setProcessedBy(librarianUsername);

        // Note: We don't decrease available copies here anymore
        // Available copies will be decreased when the loan is actually created

//        notificationService.createNotification(
//                reservation.getStudentId(),
//                String.format("Your reservation for book %s has been approved. You can now checkout the book.", book.getTitle()),
//                NotificationType.RESERVATION_APPROVED
//        );

        return reservationMapper.reservationToReservationDTO(reservationRepository.save(reservation));
    }

    private ReservationDTO rejectReservation(Reservation reservation, String librarianUsername){
        reservation.setStatus(ReservationStatus.REJECTED);
        reservation.setProcessedDate(LocalDateTime.now());
        reservation.setProcessedBy(librarianUsername);

//        notificationService.createNotification(
//                reservation.getStudentId(),
//                String.format("Your reservation for book %s has been rejected", bookService.getBookEntity(reservation.getBookId()).getTitle()),
//                NotificationType.RESERVATION_REJECTED
//        );

        return reservationMapper.reservationToReservationDTO(reservationRepository.save(reservation));
    }
}
