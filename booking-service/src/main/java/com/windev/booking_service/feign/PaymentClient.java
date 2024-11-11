package com.windev.booking_service.feign;

import com.windev.booking_service.dto.PaymentDTO;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "PAYMENT-SERVICE")
public interface PaymentClient {
    @GetMapping("/api/v1/payments")
    ResponseEntity<List<PaymentDTO>> getAllPayment();

    @GetMapping("/api/v1/payments/booking/{bookingId}")
    ResponseEntity<PaymentDTO> getPaymentByBookingId(@PathVariable("bookingId") String bookingId);
}
