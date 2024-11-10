package com.windev.booking_service.model;

import java.util.UUID;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
public class Ticket {
    @Field("ticketId")
    private String ticketId;

    @Field("seatNumber")
    private String seatNumber;

    @Field("class")
    private String ticketClass;

    @Field("price")
    private Double price;

    public Ticket(){
        ticketId = UUID.randomUUID().toString();
    }
}