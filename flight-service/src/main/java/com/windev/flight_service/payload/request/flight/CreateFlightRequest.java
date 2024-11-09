package com.windev.flight_service.payload.request.flight;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.Date;

@Data
public class CreateFlightRequest {

    @NotBlank(message = "Flight number is required")
    @Size(max = 10, message = "Flight number must not exceed 10 characters")
    private String flightNumber;

    @NotBlank(message = "Airline is required")
    @Size(max = 100, message = "Airline must not exceed 100 characters")
    private String airline;

    @NotBlank(message = "Origin is required")
    @Size(max = 100, message = "Origin must not exceed 100 characters")
    private String origin;

    @NotBlank(message = "Destination is required")
    @Size(max = 100, message = "Destination must not exceed 100 characters")
    private String destination;

    @NotNull(message = "Departure time is required")
    @Future(message = "Departure time must be in the future")
    private Date departureTime;

    @NotNull(message = "Arrival time is required")
    @Future(message = "Arrival time must be in the future")
    private Date arrivalTime;

    private String airplaneId;
}
