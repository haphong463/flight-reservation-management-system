package com.windev.notification_service.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.windev.notification_service.client.UserClient;
import com.windev.notification_service.dto.BookingEvent;
import com.windev.notification_service.dto.UserDTO;
import com.windev.notification_service.event.*;
import com.windev.notification_service.handler.NotificationStrategy;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationEventListener {
    private final Map<String, NotificationStrategy> strategies;
    private final ObjectMapper objectMapper;
    private final UserClient userClient;

    @KafkaListener(topics = "booking-topic", groupId = "${spring.kafka.consumer.group-id}")
    public void handleEvents(@Payload String message) {
        try {
            EventMessage eventMessage = objectMapper.readValue(message, EventMessage.class);

            BookingEvent data = objectMapper.convertValue(eventMessage.getData(), BookingEvent.class);
            log.info("handleEvents() --> received data: {}", data.toString());

            ResponseEntity<UserDTO> user = userClient.getUserById(data.getUserId());

            if(user.getStatusCode().is2xxSuccessful()){
                log.info("handleEvents() --> user: {}", Objects.requireNonNull(user.getBody()).toString());
            }

        } catch (Exception e) {
            log.error("handleEvents() --> Error handling event: {}", e.getMessage());
        }
    }

}
