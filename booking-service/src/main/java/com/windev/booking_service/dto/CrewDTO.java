package com.windev.booking_service.dto;

import java.util.Date;
import lombok.Data;

@Data
public class CrewDTO {
    private String code;

    private String firstName;

    private String lastName;

    private String role;

    private String licenseNumber;

    private Date issueDate;

    private Date expirationDate;

    private String status;

    private String phone;

    private Date createdAt;

    private Date updatedAt;
}
