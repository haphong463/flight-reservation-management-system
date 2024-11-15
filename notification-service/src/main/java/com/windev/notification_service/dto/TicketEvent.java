package com.windev.notification_service.dto;

import lombok.Data;

@Data
public class TicketEvent {
    private String ticketId;
    private String seatNumber;
    private String ticketClass;
    private Double price;
}
