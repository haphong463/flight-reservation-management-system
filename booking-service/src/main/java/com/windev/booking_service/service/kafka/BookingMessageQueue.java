package com.windev.booking_service.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.windev.booking_service.dto.BookingDTO;
import com.windev.booking_service.model.Booking;
import com.windev.booking_service.payload.EventMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingMessageQueue {
    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.booking.topic}")
    private String BOOKING_TOPIC;

    /**
     * Use kafkaTemplate to send event to notification
     */
    public void sendMessage(BookingDTO booking, String eventType){
        EventMessage message = EventMessage.builder()
                .data(booking)
                .eventType(eventType)
                .build();

        try {
            String eventAsString = objectMapper.writeValueAsString(message);
            kafkaTemplate.send(BOOKING_TOPIC, eventAsString);
            log.info("kafkaTemplate --> send event {} to {} successfully", eventType, BOOKING_TOPIC);
        } catch (JsonProcessingException e) {
            log.error("failed to json processing: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}