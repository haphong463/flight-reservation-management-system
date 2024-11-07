package com.windev.user_service.service;

import com.windev.user_service.dto.UserDTO;
import com.windev.user_service.payload.request.auth.SigninRequest;
import com.windev.user_service.payload.request.auth.SignupRequest;
import com.windev.user_service.payload.response.JwtResponse;
import com.windev.user_service.payload.response.UserRegisteredResponse;

public interface AuthService {
    UserRegisteredResponse register(SignupRequest req);
    JwtResponse login(SigninRequest req);
    UserDTO currentUser(String token);
    void logout(String token);
    void verifyEmail(String token);
}
