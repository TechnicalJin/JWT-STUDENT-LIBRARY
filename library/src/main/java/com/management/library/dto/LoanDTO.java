package com.management.library.dto;

import com.management.library.model.enums.LoanStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoanDTO {

    private Long id;
    private Long bookId;
    private Long studentId;
    private LocalDateTime checkOutDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;
    private LoanStatus status;
    private String librarianCheckout;
    private String librarianCheckin;
}
