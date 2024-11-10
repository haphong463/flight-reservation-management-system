package com.windev.payment_service.dto;

import java.util.List;
import lombok.Data;

@Data
public class BookingEvent {
    private String id;

    private String userId;

    private String flightId;

    private String paymentId;

    private String status;

    private List<TicketEvent> tickets;
}
