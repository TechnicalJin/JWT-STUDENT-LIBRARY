package com.management.library.controller;

import com.management.library.dto.LoanDTO;
import com.management.library.dto.ReservationDTO;
import com.management.library.dto.ReservationRequestDTO;
import com.management.library.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private static final Logger logger = LoggerFactory.getLogger(ReservationController.class);
    private final ReservationService reservationService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('STUDENT', 'USER')")
    public ResponseEntity<ReservationDTO> createReservation(@Valid @RequestBody ReservationRequestDTO requestDTO,
                                                            @AuthenticationPrincipal User user){
        logger.info("Creating reservation for student {} and book {} by user {}", 
                   requestDTO.getStudentId(), requestDTO.getBookId(), user.getUsername());
        try {
            ReservationDTO reservation = reservationService.createReservation(requestDTO, user.getUsername());
            logger.info("Successfully created reservation with ID: {}", reservation.getId());
            return new ResponseEntity<>(reservation, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating reservation: {}", e.getMessage());
            throw e;
        }
    }

    @PutMapping("/approve/{reservationId}")
    @PreAuthorize("hasAnyAuthority('LIBRARIAN', 'ADMIN')")
    public ReservationDTO approveReservation(@PathVariable Long reservationId,
                                             @AuthenticationPrincipal User user){
         return reservationService.processReservation(reservationId, "APPROVE", user.getUsername());
    }

    @PutMapping("/reject/{reservationId}")
    @PreAuthorize("hasAnyAuthority('LIBRARIAN', 'ADMIN')")
    public ReservationDTO rejectReservation(@PathVariable Long reservationId,
                                            @RequestParam String action,
                                            @AuthenticationPrincipal User user){
         return reservationService.processReservation(reservationId, action, user.getUsername());
    }

    @PutMapping("/approve-and-loan/{reservationId}")
    @PreAuthorize("hasAnyAuthority('LIBRARIAN', 'ADMIN')")
    public LoanDTO approveReservationAndCreateLoan(@PathVariable Long reservationId,
                                                   @AuthenticationPrincipal User user){
         return reservationService.approveReservationAndCreateLoan(reservationId, user.getUsername());
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyAuthority('LIBRARIAN', 'ADMIN')")
    public List<ReservationDTO> getPendingReservations(){
         return reservationService.getAllPendingReservations();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('STUDENT', 'USER')")
    public ResponseEntity<String> cancelReservation(@PathVariable Long id,
                                                    @AuthenticationPrincipal User user){
        logger.info("Cancelling reservation {} by student {}", id, user.getUsername());
        try {
            reservationService.cancelReservation(id, user.getUsername());
            logger.info("Successfully cancelled reservation {}", id);
            return ResponseEntity.ok("Reservation cancelled successfully");
        } catch (Exception e) {
            logger.error("Error cancelling reservation {}: {}", id, e.getMessage());
            throw e;
        }
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyAuthority('STUDENT', 'USER')")
    public ResponseEntity<List<ReservationDTO>> getMyReservations(@AuthenticationPrincipal User user){
        logger.info("Fetching reservations for current user: {}", user.getUsername());
        try {
            // Get student ID using email from JWT token
            String email = user.getUsername();
            Long studentId = reservationService.getStudentIdByEmail(email);
            
            List<ReservationDTO> reservations = reservationService.getReservationsByStudent(studentId);
            logger.info("Found {} reservations for student {}", reservations.size(), studentId);
            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
            logger.error("Error fetching reservations for user {}: {}", user.getUsername(), e.getMessage());
            throw e;
        }
    }

    @GetMapping("/history/student/{studentId}")
    @PreAuthorize("hasAnyAuthority('LIBRARIAN', 'ADMIN', 'STUDENT', 'USER')")
    public ResponseEntity<List<ReservationDTO>> getReservationHistory(@PathVariable Long studentId,
                                                                      @AuthenticationPrincipal User user){
        logger.info("Fetching reservation history for student {} by user {}", studentId, user.getUsername());
        try {
            // For students, they can only view their own history
            // For librarians/admins, they can view any student's history
            // You might want to add additional validation here
            
            List<ReservationDTO> reservations = reservationService.getReservationHistoryByStudent(studentId);
            logger.info("Found {} reservation records for student {}", reservations.size(), studentId);
            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
            logger.error("Error fetching reservation history for student {}: {}", studentId, e.getMessage());
            throw e;
        }
    }
}
