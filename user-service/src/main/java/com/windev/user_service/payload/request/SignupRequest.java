package com.windev.user_service.payload.request;

import com.windev.user_service.model.Preferences;
import lombok.Data;

@Data
public class SignupRequest {
    private String username;

    private String email;

    private String password;

    private Preferences preferences;
}
