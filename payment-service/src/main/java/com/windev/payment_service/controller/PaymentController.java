package com.windev.payment_service.controller;

import com.windev.payment_service.entity.Payment;
import com.windev.payment_service.service.PaymentService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<Payment> getPaymentByBookingId(@PathVariable String bookingId) {
        return ResponseEntity.ok().body(paymentService.getByBookingId(bookingId));
    }

    @GetMapping
    public ResponseEntity<List<Payment>> getPayments() {
        return ResponseEntity.ok().body(paymentService.getAllPayments());
    }
}
