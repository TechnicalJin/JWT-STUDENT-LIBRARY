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
    public ResponseEntity<ReservationDTO> createReservation(@Valid @RequestBody ReservationRequestDTO requestDTO){
        logger.info("Creating reservation for student {} and book {}", requestDTO.getStudentId(), requestDTO.getBookId());
        try {
            ReservationDTO reservation = reservationService.createReservation(requestDTO);
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
}
