package com.windev.flight_service.payload.request.crew;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.util.Date;

@Data
public class CrewRequest {

    @NotBlank(message = "First name must not be empty")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @NotBlank(message = "Last name must not be empty")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    @NotBlank(message = "Role must not be empty")
    @Pattern(regexp = "^(Pilot|Co-Pilot|Flight Attendant|Engineer)$",
            message = "Role must be Pilot, Co-Pilot, Flight Attendant, or Engineer")
    private String role;

    @NotBlank(message = "License number must not be empty")
    @Pattern(regexp = "^[A-Z0-9]{5,20}$",
            message = "License number must be between 5 and 20 characters, containing only uppercase letters and numbers")
    private String licenseNumber;

    @NotNull(message = "Issue date must not be null")
    @Past(message = "Issue date must be in the past")
    private Date issueDate;

    @NotNull(message = "Expiration date must not be null")
    @Future(message = "Expiration date must be in the future")
    private Date expirationDate;

    @NotBlank(message = "Status must not be empty")
    @Pattern(regexp = "^(Active|Inactive|On Leave)$",
            message = "Status must be Active, Inactive, or On Leave")
    private String status;

    @NotBlank(message = "Phone number must not be empty")
    @Pattern(regexp = "^(\\+84|0)[35789]\\d{8}$",
            message = "Invalid phone number format")
    private String phone;
}
