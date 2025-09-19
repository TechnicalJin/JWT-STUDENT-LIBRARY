package com.management.library.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationRequestDTO {
    
    @NotNull(message = "Book ID is required")
    private Long bookId;
    
    @NotNull(message = "Student ID is required")
    private Long studentId;
    
    @NotNull(message = "Reservation date is required")
    private LocalDate reservationDate;
}
