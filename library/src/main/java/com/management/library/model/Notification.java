//package com.management.library.model;
//
//import com.management.library.model.enums.NotificationType;
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "notification")
//@Getter
//@Setter
//@AllArgsConstructor
//@NoArgsConstructor
//@Builder
//public class Notification {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private Long recipientId;
//    private String message;
//
//    @Enumerated(EnumType.STRING)
//    private NotificationType type;
//
//    private LocalDateTime sentDate;
//    private boolean is_read;
//}
