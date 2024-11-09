package com.windev.flight_service.dto;

import com.windev.flight_service.entity.Flight;
import com.windev.flight_service.entity.SeatConfig;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class AirplaneDTO {
    private String id;

    private String name;

    private String model;

    private String manufacturer;

    private int totalSeats;

    private Date createdAt;

    private Date updatedAt;
}
