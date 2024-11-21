package com.windev.notification_service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.windev.notification_service.client.UserClient;
import com.windev.notification_service.dto.*;
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

    private static final String BOOKING_TOPIC = "booking-topic";
    private static final String USER_TOPIC = "user-topic";
    private static final String PAYMENT_TOPIC = "payment-topic";

    @KafkaListener(topics = BOOKING_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
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

    @KafkaListener(topics = PAYMENT_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
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

    @KafkaListener(topics = USER_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void handleUserEvent(@Payload String message) {
        try {
            EventMessage eventMessage = objectMapper.readValue(message, EventMessage.class);

            UserEvent user = objectMapper.convertValue(eventMessage.getData(), UserEvent.class);
            log.info("handlePaymentEvent() --> received data: {}", user.toString());

            switch (eventMessage.getEventType()) {
                case "USER-REGISTERED":
                    String linkVerify = "http://localhost:8080/api/v1/auth/verify-email/" + user.getToken();
                    emailService.sendEmailHtml(user.getEmail(),
                            "Verify account",
                            "Link verify: " + linkVerify
                    );
                    break;
                case "FORGOT_PASSWORD":
                    String linkResetPassword = "http://localhost:3000/account/forgot-password/" + user.getToken();
                    emailService.sendEmailHtml(user.getEmail(),
                            "Forgot password",
                            "Link reset password: " + linkResetPassword
                    );
                    break;
                default:
                    log.error("Event not match!!!");

            }
        } catch (Exception e) {
            log.error("handlePaymentEvent() --> Error handling event: {}", e.getMessage());
        }
    }

}