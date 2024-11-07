package com.windev.flight_service.payload.request;

import com.windev.flight_service.dto.SeatDTO;
import jakarta.validation.constraints.*;
import java.util.List;
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

    @NotNull(message = "Number of Economy seats is required")
    @Min(value = 1, message = "There must be at least one Economy seat")
    private Integer economySeats;

    @NotNull(message = "Number of Business seats is required")
    @Min(value = 1, message = "There must be at least one Business seat")
    private Integer businessSeats;

    @NotNull(message = "Number of First seats is required")
    @Min(value = 1, message = "There must be at least one Business seat")
    private Integer firstSeats;
}
