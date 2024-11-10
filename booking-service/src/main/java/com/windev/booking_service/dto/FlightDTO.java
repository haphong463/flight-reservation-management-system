package com.windev.booking_service.dto;

import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class FlightDTO {
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

    private List<CrewDTO> crews;
}
