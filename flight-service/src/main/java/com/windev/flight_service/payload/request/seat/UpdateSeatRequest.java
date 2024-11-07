package com.windev.flight_service.payload.request.seat;

import lombok.Data;

@Data
public class UpdateSeatRequest {
    private String type; // Có thể là "ECONOMY", "BUSINESS", etc.
    private Boolean isAvailable;
    private Double price;
}