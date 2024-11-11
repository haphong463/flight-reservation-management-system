package com.windev.payment_service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.windev.payment_service.dto.BookingEvent;
import com.windev.payment_service.dto.TicketEvent;
import com.windev.payment_service.entity.Payment;
import com.windev.payment_service.payload.EventMessage;
import com.windev.payment_service.repository.PaymentRepository;
import com.windev.payment_service.service.PaymentService;
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

    private final PaymentService paymentService;

    @KafkaListener(topics = "booking-topic", groupId = "${spring.kafka.consumer.group-id}")
    public void handleEvents(@Payload String message){
        try{
            EventMessage eventMessage = objectMapper.readValue(message, EventMessage.class);

            BookingEvent data = objectMapper.convertValue(eventMessage.getData(), BookingEvent.class);
            log.info("handleEvents() --> received data: {}", data.toString());

            Payment result = paymentService.createPayment(data);
            log.info("handleEvents() --> save payment ok: {}", result);
        }catch(Exception e){
            log.error("handleEvents() --> Error handling event: {}", e.getMessage());
        }
    }
}
