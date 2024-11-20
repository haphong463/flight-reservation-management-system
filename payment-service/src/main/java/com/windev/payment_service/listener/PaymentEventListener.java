package com.windev.payment_service.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.windev.payment_service.dto.BookingEvent;
import com.windev.payment_service.dto.PaymentEventDTO;
import com.windev.payment_service.dto.TicketEvent;
import com.windev.payment_service.entity.Payment;
import com.windev.payment_service.mapper.PaymentMapper;
import com.windev.payment_service.payload.EventMessage;
import com.windev.payment_service.repository.PaymentRepository;
import com.windev.payment_service.service.PaymentService;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentEventListener {
    private final ObjectMapper objectMapper;

    private final PaymentService paymentService;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final PaymentMapper paymentMapper;

    @Value("${spring.kafka.payment.topic}")
    private String PAYMENT_TOPIC;

    private static final String PAYMENT_SUCCESS_EVENT = "payment-success";
    private static final String PAYMENT_FAILED_EVENT = "payment-failed";

    @KafkaListener(topics = "booking-topic", groupId = "${spring.kafka.consumer.group-id}")
    public void handleEvents(@Payload String message){
        try{
            EventMessage eventMessage = objectMapper.readValue(message, EventMessage.class);

            BookingEvent data = objectMapper.convertValue(eventMessage.getData(), BookingEvent.class);
            log.info("handleEvents() --> received data: {}", data.toString());

            Payment result = paymentService.createPayment(data);
            log.info("handleEvents() --> save payment ok: {}", result);

            PaymentEventDTO paymentEventDTO = paymentMapper.toPaymentEventDTO(result);
            paymentEventDTO.setUserId(data.getUserId());

            sendMessage(paymentEventDTO, PAYMENT_SUCCESS_EVENT);
        }catch(Exception e){
            sendMessage(new PaymentEventDTO(), PAYMENT_FAILED_EVENT);
            log.error("handleEvents() --> Error handling event: {}", e.getMessage());
        }
    }

    private void sendMessage(PaymentEventDTO payment, String eventType){
        EventMessage message = EventMessage.builder()
                .data(payment)
                .eventType(eventType)
                .build();

        try {
            String eventAsString = objectMapper.writeValueAsString(message);
            kafkaTemplate.send(PAYMENT_TOPIC, eventAsString);
            log.info("kafkaTemplate --> send event {} to {} successfully", eventType, PAYMENT_TOPIC);
        } catch (JsonProcessingException e) {
            log.error("failed to json processing: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
