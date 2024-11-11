package com.windev.booking_service.payload;

import com.windev.booking_service.dto.PaymentDTO;
import com.windev.booking_service.model.Booking;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookingWithPaymentResponse {
    private Booking booking;
    private PaymentDTO payment;
}
