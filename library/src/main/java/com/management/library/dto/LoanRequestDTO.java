package com.management.library.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoanRequestDTO {
    private Long bookId;
    private Long studentId;
}
