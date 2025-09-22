package com.management.library.repository;

import com.management.library.model.Reservation;
import com.management.library.model.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByStatus(ReservationStatus status);
    List<Reservation> findByBookIdAndStudentIdAndStatus(Long bookId, Long studentId, ReservationStatus status);
    List<Reservation> findByStudentId(Long studentId);
    List<Reservation> findByStudentIdOrderByReservationDateDesc(Long studentId);
    List<Reservation> findByBookIdAndStudentId(Long bookId, Long studentId);
    boolean existsByBookIdAndStudentIdAndStatusIn(Long bookId, Long studentId, List<ReservationStatus> statuses);
}
