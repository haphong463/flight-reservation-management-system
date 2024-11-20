package com.windev.notification_service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.windev.notification_service.client.UserClient;
import com.windev.notification_service.dto.BookingEvent;
import com.windev.notification_service.dto.EventMessage;
import com.windev.notification_service.dto.PaymentEventDTO;
import com.windev.notification_service.dto.UserDTO;
import com.windev.notification_service.service.EmailService;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationEventListener {
    private final ObjectMapper objectMapper;
    private final UserClient userClient;
    private final EmailService emailService;

    @KafkaListener(topics = "booking-topic", groupId = "${spring.kafka.consumer.group-id}")
    public void handleBookingEvent(@Payload String message) {
        try {
            EventMessage eventMessage = objectMapper.readValue(message, EventMessage.class);

            BookingEvent data = objectMapper.convertValue(eventMessage.getData(), BookingEvent.class);
            log.info("handleBookingEvent() --> received data: {}", data.toString());

            ResponseEntity<UserDTO> res = userClient.getUserById(data.getUserId());

            if (res.getStatusCode().is2xxSuccessful()) {
                log.info("handleBookingEvent() --> received user: {}", Objects.requireNonNull(res.getBody()).toString());

                UserDTO user = res.getBody();

                emailService.sendEmailHtml(user.getEmail(), "Booking Reservation Confirmation: " + data.getId(),
                        "Booking Reservation Confirmation OK!!!");

            }
        } catch (Exception e) {
            log.error("handleBookingEvent() --> Error handling event: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "payment-topic", groupId = "${spring.kafka.consumer.group-id}")
    public void handlePaymentEvent(@Payload String message) {
        try {
            EventMessage eventMessage = objectMapper.readValue(message, EventMessage.class);

            PaymentEventDTO data = objectMapper.convertValue(eventMessage.getData(), PaymentEventDTO.class);
            log.info("handlePaymentEvent() --> received data: {}", data.toString());

            ResponseEntity<UserDTO> res = userClient.getUserById(data.getUserId());

            if (res.getStatusCode().is2xxSuccessful()) {
                log.info("handlePaymentEvent() --> received user: {}", Objects.requireNonNull(res.getBody()).toString());

                UserDTO user = res.getBody();

                emailService.sendEmailHtml(user.getEmail(), "Payment of Reservation Confirmation: " + data.getId(),
                        "Booking Reservation Confirmation "
                                + (eventMessage.getEventType().equals("payment-success") ? "SUCCESS" : "FAILED") +
                                "!!!");
            }
        } catch (Exception e) {
            log.error("handlePaymentEvent() --> Error handling event: {}", e.getMessage());
        }
    }

}