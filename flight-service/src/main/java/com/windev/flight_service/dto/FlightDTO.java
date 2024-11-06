package com.windev.flight_service.dto;

import com.windev.flight_service.entity.Seat;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
}
