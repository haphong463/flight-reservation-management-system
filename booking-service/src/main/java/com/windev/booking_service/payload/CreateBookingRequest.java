package com.windev.booking_service.payload;

import com.windev.booking_service.model.Ticket;
import java.util.List;
import lombok.Data;

@Data
public class CreateBookingRequest {
    private String id;

    private String flightId;

    private String paymentMethod;
    
    private List<Ticket> tickets;
}
