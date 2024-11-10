package com.windev.payment_service.dto;

import lombok.Data;

@Data
public class TicketEvent {
    private String ticketId;
    private String seatNumber;
    private String ticketClass;
    private Double price;
}
