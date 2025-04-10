//package com.management.library.service;
//
//import com.management.library.dto.NotificationDTO;
//import com.management.library.mapper.NotificationMapper;
//import com.management.library.model.Notification;
//import com.management.library.model.enums.NotificationType;
//import com.management.library.repository.NotificationRepository;
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class NotificationService {
//    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
//
//    private final NotificationRepository notificationRepository;
//    private final NotificationMapper notificationMapper;
//
//    @Transactional
//    public NotificationDTO createNotification(Long recipientId, String message, NotificationType type){
//        logger.info("Creating {} notification for user {}", type, recipientId);
//
//        Notification notification = new Notification();
//        notification.setRecipientId(recipientId);
//        notification.setMessage(message);
//        notification.setType(type);
//        notification.setSentDate(LocalDateTime.now());
//        notification.set_read(false);
//
//        Notification saved = notificationRepository.save(notification);
//        logger.debug("Created notification ID: {}", saved.getId());
//
//        return notificationMapper.notificationToNotificationDTO(saved);
//    }
//
//    @Transactional
//    public void markAsRead(Long notificationId){
//        logger.info("Marking notification {} as read",notificationId);
//        notificationRepository.findById(notificationId).ifPresent(notification -> {
//            notification.set_read(true);
//            notificationRepository.save(notification);
//        });
//    }
//
//    public List<NotificationDTO> getUnreadNotifications(Long recipientId){
//        logger.debug("Fetching unread notifications for user {}", recipientId);
//
//        return notificationRepository.findByRecipientIdAndReadFalseOrderBySentDateDesc(recipientId)
//                .stream()
//                .map(notificationMapper::notificationToNotificationDTO)
//                .collect(Collectors.toList());
//    }
//}
