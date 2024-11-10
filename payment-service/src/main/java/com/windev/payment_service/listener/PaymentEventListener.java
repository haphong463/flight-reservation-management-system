package com.windev.payment_service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.windev.payment_service.dto.BookingEvent;
import com.windev.payment_service.dto.TicketEvent;
import com.windev.payment_service.entity.Payment;
import com.windev.payment_service.payload.EventMessage;
import com.windev.payment_service.repository.PaymentRepository;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentEventListener {
    private final ObjectMapper objectMapper;

    private final PaymentRepository paymentRepository;

    @KafkaListener(topics = "booking-topic", groupId = "${spring.kafka.consumer.group-id}")
    public void handleEvents(@Payload String message){
        try{
            EventMessage eventMessage = objectMapper.readValue(message, EventMessage.class);

            BookingEvent data = objectMapper.convertValue(eventMessage.getData(), BookingEvent.class);

            Payment payment = new Payment();


            BigDecimal total = BigDecimal.ZERO;

            for(TicketEvent ticketEvent : data.getTickets()){
                if (ticketEvent.getPrice() != null) {
                    // Chuyển đổi giá từ Double sang BigDecimal
                    BigDecimal ticketPrice = BigDecimal.valueOf(ticketEvent.getPrice());
                    total = total.add(ticketPrice);
                }
            }

            payment.setBookingId(data.getId());
            payment.setAmount(total);
            payment.setPaymentMethod("PAYPAL");
            payment.setStatus("PENDING");


            payment.setPaymentMethod("PAYPAL");

            paymentRepository.save(payment);
            log.info("handleEvents() --> received data: {}", data.toString());

        }catch(Exception e){
            log.error("handleEvents() --> Error handling event: {}", e.getMessage());
        }
    }
}
