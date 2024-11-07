package com.windev.user_service.payload.request.password;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PasswordResetRequest {
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*\\W)(?!.*\\s).{8,16}$",
            message = "Password must be 8-16 characters long, including uppercase and lowercase letters, numbers, and special characters."
    )
    private String newPassword;
    private String confirmNewPassword;
}
