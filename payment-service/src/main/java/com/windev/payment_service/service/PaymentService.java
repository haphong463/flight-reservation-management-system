package com.windev.payment_service.service;

import com.windev.payment_service.dto.BookingEvent;
import com.windev.payment_service.entity.Payment;
import java.util.List;

public interface PaymentService {
    Payment createPayment(BookingEvent data);
    List<Payment> getAllPayments();
    Payment getByBookingId(String bookingId);
}
