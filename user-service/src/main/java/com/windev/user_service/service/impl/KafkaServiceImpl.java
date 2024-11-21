package com.windev.user_service.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.windev.user_service.event.PasswordForgotEvent;
import com.windev.user_service.event.UserRegisteredEvent;
import com.windev.user_service.model.User;
import com.windev.user_service.payload.response.EventMessage;
import com.windev.user_service.service.KafkaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaServiceImpl implements KafkaService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.user.topic}")
    private String USER_TOPIC;

    @Override
    /**
     * Use kafkaTemplate to send event to notification
     */
    public void sendUserRegisteredMessage(UserRegisteredEvent event, String eventType) {
        EventMessage message = EventMessage.builder()
                .data(event)
                .eventType(eventType)
                .build();

        try {
            String eventAsString = objectMapper.writeValueAsString(message);
            kafkaTemplate.send(USER_TOPIC, eventAsString);
            log.info("kafkaTemplate --> send user registered event {} to {} successfully", eventType, USER_TOPIC);
        } catch (JsonProcessingException e) {
            log.error("failed to json processing: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendPasswordForgotMessage(PasswordForgotEvent event, String eventType) {
        EventMessage message = EventMessage.builder()
                .data(event)
                .eventType(eventType)
                .build();

        try {
            String eventAsString = objectMapper.writeValueAsString(message);
            kafkaTemplate.send(USER_TOPIC, eventAsString);
            log.info("kafkaTemplate --> send password forgot event {} to {} successfully", eventType, USER_TOPIC);
        } catch (JsonProcessingException e) {
            log.error("failed to json processing: {}", e.getMessage());
        }
    }
}
