package com.windev.flight_service.payload.request.flight;

import lombok.Data;

@Data
public class UpdateFlightStatusRequest {
    private String status;
}
