package com.windev.booking_service.model;

import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@Document(collection = "bookings")
public class Booking {
    @Id
    private String id;

    private String userId;

    private String flightId;

    private String paymentId;

    private String status;

    private List<Ticket> tickets;

    private Date createdAt;

    private Date updatedAt;

    public Booking(){
        createdAt = new Date();
    }
}
