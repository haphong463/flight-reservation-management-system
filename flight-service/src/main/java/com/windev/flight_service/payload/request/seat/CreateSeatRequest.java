package com.windev.flight_service.payload.request.seat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateSeatRequest {
    private String seatNumber;

    private String type;

    private Boolean isAvailable;

    private Double price;
}
