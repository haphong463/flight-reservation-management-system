package com.windev.user_service.payload.request.password;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordForgotRequest {
    @NotBlank(message = "Email must not be blank.")
    @Email(message = "Please provide a valid email address.")
    private String email;
}
