package com.windev.user_service.controller;

import com.windev.user_service.dto.UserDTO;
import com.windev.user_service.payload.request.auth.SigninRequest;
import com.windev.user_service.payload.request.auth.SignupRequest;
import com.windev.user_service.payload.response.JwtResponse;
import com.windev.user_service.payload.response.UserRegisteredResponse;
import com.windev.user_service.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for authenticating users")

public class AuthController {
    private final AuthService authService;

    /**
     * Register a new user account.
     *
     * @param req SignupRequest containing user registration details.
     * @return ResponseEntity with UserRegisteredResponse or error message.
     */
    @Operation(
            summary = "Register an Account",
            description = "Register a new user account by providing necessary details."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Account registered successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid SignupRequest req) {
        try {
            UserRegisteredResponse user = authService.register(req);
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * User login and JWT token generation.
     *
     * @param req SigninRequest containing user credentials.
     * @return ResponseEntity with JwtResponse or error message.
     */
    @Operation(
            summary = "User Login",
            description = "Authenticate user and generate JWT token."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Login successful, JWT token generated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody SigninRequest req) {
        try {
            JwtResponse response = authService.login(req);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieve current authenticated user information.
     *
     * @param authHeader Authorization header containing Bearer token.
     * @return ResponseEntity with UserDTO or error message.
     */
    @Operation(
            summary = "Get Current User",
            description = "Retrieve information of the currently authenticated user.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved current user information"),
            @ApiResponse(responseCode = "400", description = "Bad request due to missing or invalid token"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/me")
    public ResponseEntity<?> currentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                UserDTO currentUser = authService.currentUser(token);

                return new ResponseEntity<>(currentUser, HttpStatus.OK);
            }
            return new ResponseEntity<>("Invalid Authorization header", HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Logout the current user by invalidating the JWT token.
     *
     * @param authHeader Authorization header containing Bearer token.
     * @return ResponseEntity with success or error message.
     */
    @Operation(
            summary = "Logout User",
            description = "Logout the currently authenticated user by invalidating their JWT token.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout successful"),
            @ApiResponse(responseCode = "400", description = "Bad request due to missing or invalid token"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                authService.logout(token);
                return new ResponseEntity<>("Logout successful", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Invalid Authorization header", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Verify user's email address using a token.
     *
     * @param token Verification token sent to user's email.
     * @return ResponseEntity with success or error message.
     */
    @Operation(
            summary = "Verify Email",
            description = "Verify the user's email address using the provided token."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email verified successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/verify-email/{token}")
    public ResponseEntity<?> verifyEmail(@PathVariable String token) {
        try {
            authService.verifyEmail(token);
            return new ResponseEntity<>("Email verification successful", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
