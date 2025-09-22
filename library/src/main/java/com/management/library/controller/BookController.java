package com.management.library.controller;

import com.management.library.dto.BookDTO;
import com.management.library.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private static final Logger logger = LoggerFactory.getLogger(BookController.class);
    private final BookService bookService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<BookDTO> createBook(@Valid @RequestBody BookDTO bookDTO){
        logger.info("Creating new book: {}", bookDTO.getTitle());
        try {
            BookDTO createdBook = bookService.createBook(bookDTO);
            logger.info("Book created successfully with ID: {}", createdBook.getId());
            return new ResponseEntity<>(createdBook, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating book: {}", e.getMessage());
            throw e;
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'LIBRARIAN', 'STUDENT', 'USER')")
    public ResponseEntity<List<BookDTO>> getAllBooks(){
        logger.info("Fetching all books");
        try {
            List<BookDTO> books = bookService.getAllBooks();
            logger.info("Found {} books", books.size());
            return ResponseEntity.ok(books);
        } catch (Exception e) {
            logger.error("Error fetching books: {}", e.getMessage());
            throw e;
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'LIBRARIAN', 'STUDENT', 'USER')")
    public ResponseEntity<BookDTO> getBookById(@PathVariable Long id){
        logger.info("Fetching book with ID: {}", id);
        BookDTO book = bookService.getBookById(id);
        return ResponseEntity.ok(book);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<BookDTO> updateBook(@PathVariable Long id, @Valid @RequestBody BookDTO bookDTO){
        logger.info("Updating book with ID: {}", id);
        BookDTO updatedBook = bookService.updateBook(id, bookDTO);
        logger.info("Book updated successfully: {}", updatedBook.getTitle());
        return ResponseEntity.ok(updatedBook);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<String> deleteBook(@PathVariable Long id){
        logger.info("Deleting book with ID: {}", id);
        bookService.deleteBook(id);
        logger.info("Book deleted successfully with ID: {}", id);
        return ResponseEntity.ok("Book deleted successfully");
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'LIBRARIAN', 'STUDENT', 'USER')")
    public ResponseEntity<List<BookDTO>> searchBooks(@RequestParam String title){
        logger.info("Searching books with title: {}", title);
        try {
            List<BookDTO> books = bookService.searchBooksByTitle(title);
            logger.info("Found {} books matching title: {}", books.size(), title);
            return ResponseEntity.ok(books);
        } catch (Exception e) {
            logger.error("Error searching books: {}", e.getMessage());
            throw e;
        }
    }

    @GetMapping("/{id}/availability")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'LIBRARIAN', 'STUDENT', 'USER')")
    public ResponseEntity<Boolean> checkBookAvailability(@PathVariable Long id){
        logger.info("Checking availability for book with ID: {}", id);
        try {
            boolean available = bookService.isBookAvailable(id);
            logger.info("Book ID: {} availability status: {}", id, available);
            return ResponseEntity.ok(available);
        } catch (Exception e) {
            logger.error("Error checking book availability: {}", e.getMessage());
            throw e;
        }
    }
}