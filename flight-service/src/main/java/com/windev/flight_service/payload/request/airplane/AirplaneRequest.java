package com.windev.flight_service.payload.request.airplane;

import com.windev.flight_service.dto.SeatConfigDTO;
import java.util.List;
import lombok.Data;

@Data
public class AirplaneRequest {
    private String name;

    private String model;

    private String manufacturer;

    private int totalSeats;

    private List<SeatConfigDTO> seatConfigs;
}
