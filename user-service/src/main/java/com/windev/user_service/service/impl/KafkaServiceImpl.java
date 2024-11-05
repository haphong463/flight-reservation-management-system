package com.windev.user_service.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    @Value("${spring.kafka.notification.topic}")
    private String NOTIFICATION_TOPIC;

    @Override
    /**
     * Use kafkaTemplate to send event to notification
     */
    public void sendMessage(User user, String eventType){
        EventMessage message = EventMessage.builder()
                .data(user)
                .eventType(eventType)
                .build();

        try {
            String eventAsString = objectMapper.writeValueAsString(message);
            kafkaTemplate.send(NOTIFICATION_TOPIC, eventAsString);
            log.info("kafkaTemplate --> send event {} to {} successfully", eventType, NOTIFICATION_TOPIC);
        } catch (JsonProcessingException e) {
            log.error("failed to json processing: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
