package com.management.library.service;

import com.management.library.dto.BookDTO;
import com.management.library.exception.BookNotAvailableException;
import com.management.library.mapper.BookMapper;
import com.management.library.model.Book;
import com.management.library.model.enums.BookStatus;
import com.management.library.repository.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private static final Logger logger = LoggerFactory.getLogger(BookService.class);

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Transactional
    public BookDTO createBook(BookDTO bookDTO){
        logger.info("Creating new book with title : {}", bookDTO.getTitle());
        Book book = bookMapper.bookDTOToBook(bookDTO);
        book.setStatus(BookStatus.AVAILABLE);
        book.setAvailableCopies(book.getTotalCopies());
        Book savedBook = bookRepository.save(book);

        logger.debug("Created Book with ID: {}", savedBook.getId());
        return bookMapper.bookToBookDTO(savedBook);
    }

    public List<BookDTO> getAllBooks(){
        logger.info("Fetching All Books");
        return bookRepository.findAll().stream()
                .map(bookMapper::bookToBookDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookDTO getBookById(Long id){
        logger.info("Fetching Book By ID: {}", id);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with ID: " + id)); // Simplified message
        return bookMapper.bookToBookDTO(book);
    }

    @Transactional
    public BookDTO updateBook(Long id, BookDTO bookDTO){
        logger.info("Updating Book with ID: {}", id);
        Book existingBook = bookRepository.findById(id)
                .orElseThrow( ()-> new EntityNotFoundException("Book Not Found"));

        bookMapper.updateBookFromDTO(bookDTO, existingBook);
        validateCopiesConsistency(existingBook);
        updateBookStatus(existingBook);

//        int checkOutCopies = existingBook.getTotalCopies() - existingBook.getAvailableCopies();
//        int newAvailableCopies = bookDTO.getTotalCopies() - checkOutCopies;
//
//        logger.debug("Updating book details. Previous title: {}, new title: {}", existingBook.getTitle(), bookDTO.getTitle());
//
//        existingBook.setTitle(bookDTO.getTitle());
//        existingBook.setAuthor(bookDTO.getAuthor());
//        existingBook.setIsbn(bookDTO.getIsbn());
//        existingBook.setTotalCopies(bookDTO.getTotalCopies());
//        existingBook.setAvailableCopies(bookDTO.getAvailableCopies());
//        existingBook.setStatus(bookDTO.getTotalCopies() > 0 ?
//                BookStatus.AVAILABLE : BookStatus.UNAVAILABLE);

        Book updateBook = bookRepository.save(existingBook);
        logger.debug("Book with ID: {} updated successfully", id);
        return bookMapper.bookToBookDTO(updateBook);
    }

    @Transactional
    public void deleteBook(Long id){
        logger.info("Deleting Book with ID: {}", id);
        if (!bookRepository.existsById(id)){
            throw new BookNotAvailableException("Book Not Found with ID: " + id);
        }

        bookRepository.deleteById(id);
        logger.debug("Deleted Book With ID: {}", id);
    }

    @Transactional
    public void updateAvailableCopies(Long bookId, int newAvailableCopies){
        logger.info("Updating Available copies for book ID: {}", bookId);

        Book book = getBookEntity(bookId);
        if (newAvailableCopies < 0 || newAvailableCopies > book.getTotalCopies()){
            throw new IllegalArgumentException("Invalid available Copies count");
        }

        book.setAvailableCopies(newAvailableCopies);
        updateBookStatus(book);
        bookRepository.save(book);
    }

    public Book getBookEntity(Long bookId){
        logger.debug("Fetching book entity for ID: {}", bookId);
        return bookRepository.findByIdWithLock(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));
    }

    @Transactional
    public void updateBook(Book book){
        logger.debug("Saving book updates for ID: {}", book.getId());
        validateCopiesConsistency(book);
        updateBookStatus(book);
        bookRepository.save(book);
    }

    private void validateCopiesConsistency(Book book){
        if (book.getAvailableCopies() > book.getTotalCopies()){
            throw new IllegalArgumentException("Available copies can not exceed total copies");
        }
        if (book.getAvailableCopies() < 0){
            throw new IllegalArgumentException("Available copies cannot be negative");
        }
    }

    private void updateBookStatus(Book book){
        if (book.getAvailableCopies() == 0){
            book.setStatus(BookStatus.LOANED);
        } else if (book.getAvailableCopies() > 0) {
            book.setStatus(BookStatus.AVAILABLE);
        }else {
            book.setStatus(BookStatus.UNAVAILABLE);
        }
    }
}
