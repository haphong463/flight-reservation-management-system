package com.windev.user_service.payload.request.auth;

import com.windev.user_service.model.Preferences;
import com.windev.user_service.payload.request.user_profile.UserProfileRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SignupRequest {

    @NotBlank(message = "Username must not be blank.")
    private String username;

    @NotBlank(message = "Email must not be blank.")
    @Email(message = "Please provide a valid email address.")
    private String email;


    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*\\W)(?!.*\\s).{8,16}$",
            message = "Password must be 8-16 characters long, including uppercase and lowercase letters, numbers, and special characters."
    )
    private String password;

    @Valid
    private UserProfileRequest userProfileRequest;

    private Preferences preferences;
}
