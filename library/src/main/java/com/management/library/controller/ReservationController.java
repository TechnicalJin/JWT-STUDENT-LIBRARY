package com.management.library.controller;

import com.management.library.dto.ReservationDTO;
import com.management.library.dto.ReservationRequestDTO;
import com.management.library.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

     @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationDTO createReservation(@RequestBody ReservationRequestDTO requestDTO){
         return reservationService.createReservation(requestDTO);
    }

    @PutMapping("/approve/{reservationId}")
    public ReservationDTO approveReservation(@PathVariable Long reservationId,
                                             @AuthenticationPrincipal User user){
         return reservationService.processReservation(reservationId, "APPROVE", user.getUsername());
    }

    @PutMapping("/reject/{reservationId}")
    public ReservationDTO rejectReservation(@PathVariable Long reservationId,
                                            @RequestParam String action,
                                            @AuthenticationPrincipal User user){
         return reservationService.processReservation(reservationId, action, user.getUsername());
    }
}
