package com.windev.flight_service.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.windev.flight_service.dto.FlightDetailDTO;
import com.windev.flight_service.payload.response.EventMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FlightMessageQueue {
    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.notification.topic}")
    private String NOTIFICATION_TOPIC;

    /**
     * Use kafkaTemplate to send event to notification
     */
    public void sendMessage(FlightDetailDTO flight, String eventType){
        EventMessage message = EventMessage.builder()
                .data(flight)
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
