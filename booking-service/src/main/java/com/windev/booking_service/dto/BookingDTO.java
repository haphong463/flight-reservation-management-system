package com.windev.booking_service.dto;

import com.windev.booking_service.model.Ticket;
import java.util.List;
import lombok.Data;

@Data
public class BookingDTO {
    private String id;

    private String userId;

    private String flightId;

    private String paymentId;

    private String status;

    private List<Ticket> tickets;
}
