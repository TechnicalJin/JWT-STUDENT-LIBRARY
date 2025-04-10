package com.management.library.dto;

import com.management.library.model.enums.ReservationStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationDTO {
    private Long id;
    private Long bookId;
    private Long studentId;
    private LocalDateTime reservationDate;
    private ReservationStatus status;
    private LocalDateTime processedDate;
    private String processedBy;
}
