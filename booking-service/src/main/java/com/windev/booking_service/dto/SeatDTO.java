package com.windev.booking_service.dto;

import java.util.Date;
import lombok.Data;

@Data
public class SeatDTO {
    private Long id;

    private String seatNumber;

    private String type;

    private Boolean isAvailable;

    private Double price;

    private Date createdAt;

    private Date updatedAt;
}
