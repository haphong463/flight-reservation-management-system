package com.windev.user_service.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateUserRequest {

    @NotBlank(message = "First name must not be blank!")
    private String firstName;

    @NotBlank(message = "Last name must not be blank!")
    private String lastName;
}
