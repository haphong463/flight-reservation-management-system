package com.windev.payment_service.service.impl;

import com.windev.payment_service.dto.BookingEvent;
import com.windev.payment_service.dto.TicketEvent;
import com.windev.payment_service.entity.Payment;
import com.windev.payment_service.exception.PaymentNotFoundException;
import com.windev.payment_service.repository.PaymentRepository;
import com.windev.payment_service.service.PaymentService;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;

    @Override
    public Payment createPayment(BookingEvent data) {

        Payment payment = new Payment();


        BigDecimal total = BigDecimal.ZERO;

        for(TicketEvent ticketEvent : data.getTickets()){
            if (ticketEvent.getPrice() != null) {
                BigDecimal ticketPrice = BigDecimal.valueOf(ticketEvent.getPrice());
                total = total.add(ticketPrice);
            }
        }

        payment.setId(data.getPaymentId());
        payment.setBookingId(data.getId());
        payment.setAmount(total);
        payment.setPaymentMethod(data.getPaymentMethod());
        payment.setStatus("PENDING");
        return paymentRepository.save(payment);
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Override
    public Payment getByBookingId(String bookingId) {
        return paymentRepository.findByBookingId(bookingId).orElseThrow(() -> new PaymentNotFoundException("Payment " +
                "not found with booking ID: " + bookingId));
    }
}
