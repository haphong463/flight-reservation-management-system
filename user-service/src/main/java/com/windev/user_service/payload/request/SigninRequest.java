package com.windev.user_service.payload.request;

import lombok.Data;

@Data
public class SigninRequest {
    private String username;

    private String password;
}
