package com.windev.user_service.dto;

import com.windev.user_service.model.Address;
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
