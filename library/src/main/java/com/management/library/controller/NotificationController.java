//package com.management.library.controller;
//
//import com.management.library.dto.NotificationDTO;
//import com.management.library.service.NotificationService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/notifications")
//@RequiredArgsConstructor
//public class NotificationController {
//    private final NotificationService notificationService;
//
//    @GetMapping("/user/{userId}")
//    public List<NotificationDTO> getUserNotifications(@PathVariable Long userId){
//        return notificationService.getUnreadNotifications(userId);
//    }
//
//    @PatchMapping("/{notificationId}/read")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void markNotificationAsRead(@PathVariable Long notificationId){
//        notificationService.markAsRead(notificationId);
//    }
//
////    @PatchMapping("/{notificationId}/read")
////    @ResponseStatus(HttpStatus.NO_CONTENT)
////    public LoanDTO checkoutBook(@RequestBody LoanRequestDTO requestDTO, @AuthenticationPrincipal UserDetails userDetails){
////        LoanDTO loan = loanService.checkOutBook(requestDTO);
////        loan.setLibrarianCheckout(userDetails.getUsername());
////        return loan;
////    }
////
////    @PutMapping("/return/{loanId}")
////    public LoanDTO returnBook(@PathVariable Long loanId, @AuthenticationPrincipal UserDetails userDetails){
////        LoanDTO loan = loanService.returnBook(loanId);
////        loan.setLibrarianCheckin(userDetails.getUsername());
////        return loan;
////    }
//}
