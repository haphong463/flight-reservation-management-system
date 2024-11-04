package com.windev.user_service.service;

import com.windev.user_service.model.User;
import com.windev.user_service.payload.request.SigninRequest;
import com.windev.user_service.payload.request.SignupRequest;
import java.util.Optional;

public interface AuthService {
    User register(SignupRequest req);
    String login(SigninRequest req);
}
