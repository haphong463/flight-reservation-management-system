package com.windev.user_service.payload.request.user_profile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Date;
import lombok.Data;

@Data
public class UserProfileRequest {

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must be at most 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must be at most 50 characters")
    private String lastName;

    @Size(max = 15, message = "Phone number must be at most 15 characters")
    private String phone;

    private Date dob;

    @Valid
    private AddressRequest address;

}
