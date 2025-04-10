//package com.management.library.repository;
//
//import com.management.library.model.Notification;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.List;
//
//public interface NotificationRepository extends JpaRepository<Notification, Long> {
//    List<Notification> findByRecipientIdAndReadFalseOrderBySentDateDesc(Long recipientId);
//    List<Notification> findByRecipientIdOrderBySentDateDesc(Long recipientId);
//}
