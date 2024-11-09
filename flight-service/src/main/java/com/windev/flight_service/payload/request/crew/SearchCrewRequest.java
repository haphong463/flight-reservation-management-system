package com.windev.flight_service.payload.request.crew;

import java.util.Date;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class SearchCrewRequest {
    private String code;

    private String firstName;

    private String lastName;

    private String role;

    private String licenseNumber;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date issueDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date expirationDate;

    private String status;

    private String phone;
}
