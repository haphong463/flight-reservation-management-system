package com.windev.user_service.controller;

import com.windev.user_service.model.User;
import com.windev.user_service.payload.request.SigninRequest;
import com.windev.user_service.payload.request.SignupRequest;
import com.windev.user_service.service.AuthService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    // register
    // login
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody SignupRequest req){
        try {
            User user = authService.register(req);
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        }catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody SigninRequest req){
        try {
            String token = authService.login(req);
            return new ResponseEntity<>(token, HttpStatus.CREATED);
        }catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
