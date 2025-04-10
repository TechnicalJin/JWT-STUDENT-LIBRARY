package com.management.library.repository;

import com.management.library.model.Reservation;
import com.management.library.model.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByStatus(ReservationStatus status);
    List<Reservation> findByBookIdAndStudentIdAndStatus(Long bookId, Long studentId, ReservationStatus status);
}
