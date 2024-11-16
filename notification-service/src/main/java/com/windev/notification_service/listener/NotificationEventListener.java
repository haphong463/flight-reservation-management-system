package com.windev.notification_service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.windev.notification_service.client.UserClient;
import com.windev.notification_service.dto.BookingEvent;
import com.windev.notification_service.dto.EventMessage;
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
    public void handleEvents(@Payload String message) {
        try {
            EventMessage eventMessage = objectMapper.readValue(message, EventMessage.class);

            BookingEvent data = objectMapper.convertValue(eventMessage.getData(), BookingEvent.class);
            log.info("handleEvents() --> received data: {}", data.toString());

            ResponseEntity<UserDTO> res = userClient.getUserById(data.getUserId());

            if (res.getStatusCode().is2xxSuccessful()) {
                log.info("handleEvents() --> received user: {}", Objects.requireNonNull(res.getBody()).toString());

                UserDTO user = res.getBody();

                emailService.sendEmailHtml(user.getEmail(), "Booking Reservation Confirmation: " + data.getId(),
                        "Booking Reservation Confirmation OK!!!");

            }
        } catch (Exception e) {
            log.error("handleEvents() --> Error handling event: {}", e.getMessage());
        }
    }

}