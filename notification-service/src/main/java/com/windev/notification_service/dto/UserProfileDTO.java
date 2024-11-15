package com.windev.notification_service.dto;

import java.util.Date;
import lombok.Data;

@Data
public class UserProfileDTO {
    private String firstName;
    private String lastName;
    private Address address;
    private String phone;
    private Date dob;
}
