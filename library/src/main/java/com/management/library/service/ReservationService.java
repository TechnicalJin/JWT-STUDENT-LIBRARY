package com.management.library.service;

import com.management.library.dto.ReservationDTO;
import com.management.library.dto.ReservationRequestDTO;
import com.management.library.dto.StudentDTO;
import com.management.library.exception.InvalidReservationStatusException;
import com.management.library.exception.ReservationNotFoundException;
import com.management.library.exception.StudentNotFoundException;
import com.management.library.mapper.ReservationMapper;
import com.management.library.model.Book;
import com.management.library.model.Reservation;
//import com.management.library.model.enums.NotificationType;
import com.management.library.model.enums.ReservationStatus;
import com.management.library.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private static final Logger logger = LoggerFactory.getLogger(ReservationService.class);

    @Autowired
    private RestTemplate restTemplate;

    private final ReservationRepository reservationRepository;
    private final BookService bookService;
    private final StudentServiceClient studentServiceClient;
//    private final NotificationService notificationService;
    private final ReservationMapper reservationMapper;

    @Transactional
    public ReservationDTO createReservation(ReservationRequestDTO requestDTO) {
        String url = "http://localhost:8081/api/students/exists/" + requestDTO.getStudentId();
        Boolean studentExists = restTemplate.getForObject(url, Boolean.class);
        if (!Boolean.TRUE.equals(studentExists)) {
            throw new StudentNotFoundException("Student not found with ID : " + requestDTO.getStudentId());
        }

        Reservation reservation = new Reservation();
        reservation.setBookId(requestDTO.getBookId());
        reservation.setStudentId(requestDTO.getStudentId());
        reservation.setReservationDate(LocalDateTime.now());
        reservation.setStatus(ReservationStatus.PENDING);

        return reservationMapper.reservationToReservationDTO(reservationRepository.save(reservation));
    }

    @Transactional
    public void cancelReservationForCheckout(Long bookId, Long studentId){
        List<Reservation> reservations = reservationRepository.findByBookIdAndStudentIdAndStatus(bookId, studentId, ReservationStatus.APPROVED);

        if (!reservations.isEmpty()){
            reservations.forEach(reservation -> {
                reservation.setStatus(ReservationStatus.CANCELLED);
                reservationRepository.save(reservation);
                logger.info("Cancelled reservation ID: {} for checkout", reservation.getId());
            });
        }else {
            throw new ReservationNotFoundException("No approved reservation found");
        }
    }

    @Transactional
    public ReservationDTO processReservation(Long reservationId, String action, String librarianUsername){
        logger.info("Processing reservation {} with action {} by librarian {}", reservationId, action, librarianUsername);

        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(()-> new ReservationNotFoundException("Reservation not found"));
        if (action.equalsIgnoreCase("APPROVE")){
            return approveReservation(reservation, librarianUsername);
        } else if (action.equalsIgnoreCase("REJECT")) {
            return rejectReservation(reservation, librarianUsername);
        }

        throw new InvalidReservationStatusException("Invalid action : ", action);
    }

    private ReservationDTO approveReservation(Reservation reservation, String librarianUsername){
        reservation.setStatus(ReservationStatus.APPROVED);
        reservation.setProcessedDate(LocalDateTime.now());
        reservation.setProcessedBy(librarianUsername);

        Book book = bookService.getBookEntity(reservation.getBookId());
        if (book.getAvailableCopies() > 0){
            book.setAvailableCopies(book.getAvailableCopies() - 1);
            bookService.updateBook(book);
        }

//        notificationService.createNotification(
//                reservation.getStudentId(),
//                String.format("Your reservation for book %s has been approved", book.getTitle()),
//                NotificationType.RESERVATION_APPROVED
//        );

        return reservationMapper.reservationToReservationDTO(reservationRepository.save(reservation));
    }

    private ReservationDTO rejectReservation(Reservation reservation, String librarianUsername){
        reservation.setStatus(ReservationStatus.REJECTED);
        reservation.setProcessedDate(LocalDateTime.now());
        reservation.setProcessedBy(librarianUsername);

//        notificationService.createNotification(
//                reservation.getStudentId(),
//                String.format("Your reservation for book %s has been rejected", bookService.getBookEntity(reservation.getBookId()).getTitle()),
//                NotificationType.RESERVATION_REJECTED
//        );

        return reservationMapper.reservationToReservationDTO(reservationRepository.save(reservation));
    }
}
