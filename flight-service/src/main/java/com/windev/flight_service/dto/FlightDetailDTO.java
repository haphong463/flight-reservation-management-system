package com.windev.flight_service.dto;

import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class FlightDetailDTO {
    private String id;

    private String flightNumber;

    private String airline;

    private String origin;

    private String destination;

    private Date departureTime;

    private Date arrivalTime;

    private String status;

    private Date createdAt;

    private Date updatedAt;

    private List<SeatDTO> seats;
}
