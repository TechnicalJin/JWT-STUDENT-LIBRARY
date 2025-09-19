package com.management.library.dto;

import com.management.library.model.enums.BookStatus;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookDTO {
    private Long id;
    
    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    private String title;
    
    @NotBlank(message = "Author is required")
    @Size(min = 1, max = 255, message = "Author must be between 1 and 255 characters")
    private String author;
    
    @NotBlank(message = "ISBN is required")
    @Pattern(regexp = "^(?:\\d{10}|\\d{13}|\\d{3}-\\d{10}|\\d{3}-\\d{1}-\\d{3}-\\d{5}-\\d{1})$",
             message = "Invalid ISBN format")
    private String isbn;
    
    private String genre;
    
    @Min(value = 1, message = "Total quantity must be at least 1")
    @Max(value = 10000, message = "Total quantity cannot exceed 10000")
    private int totalQuantity;
    
    @Min(value = 0, message = "Available quantity cannot be negative")
    private int availableQuantity;
    
    private BookStatus status;
}
