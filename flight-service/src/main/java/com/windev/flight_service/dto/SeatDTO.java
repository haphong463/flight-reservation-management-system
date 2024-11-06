package com.windev.flight_service.dto;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeatDTO {
    private Long id;

    private String seatNumber;

    private String type;

    private Boolean isAvailable;

    private Double price;

    private Date createdAt;

    private Date updatedAt;
}
