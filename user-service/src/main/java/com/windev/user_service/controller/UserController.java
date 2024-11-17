package com.windev.user_service.controller;

import com.windev.user_service.dto.UserDTO;
import com.windev.user_service.model.Authority;
import com.windev.user_service.payload.request.password.PasswordChangeRequest;
import com.windev.user_service.payload.request.password.PasswordForgotRequest;
import com.windev.user_service.payload.request.password.PasswordResetRequest;
import com.windev.user_service.payload.request.user.AuthorityRequest;
import com.windev.user_service.payload.request.user_profile.UserProfileRequest;
import com.windev.user_service.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/users")
@RestController
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {

    private final UserService userService;

    /**
     * Update User
     *
     * @param id
     * @param request
     * @return user
     */
    @PatchMapping("{id}")
    @Operation(
            summary = "Update User Information",
            description = "Updates detailed information of a user based on their ID.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody @Valid UserProfileRequest request) {
        try {
            return new ResponseEntity<>(userService.updateUser(id, request), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * GET All Users
     *
     * @param pageable
     * @return
     */
    @GetMapping
    @Operation(
            summary = "Retrieve All Users",
            description = "Returns a paginated list of all users.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user list"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> getAllUsers(@RequestParam(defaultValue = "0") int pageNumber,
                                         @RequestParam(defaultValue = "10") int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            return new ResponseEntity<>(userService.getAllUsers(pageable), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/ids")
    public ResponseEntity<?> getAllUsers(@RequestParam("ids") Set<String> ids
    ) {
        try {
            return new ResponseEntity<>(userService.findUserByIds(ids), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Get User By Id
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @Operation(
            summary = "Get User by ID",
            description = "Retrieves detailed information of a user by their ID.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user information"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<UserDTO> getUserById(@PathVariable String id) {
        return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
    }

    /**
     * Change User's Password
     *
     * @param id
     * @param request
     * @return
     */
    @PatchMapping("{id}/change-password")
    @Operation(
            summary = "Change User Password",
            description = "Changes the password of a user based on their ID.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password updated successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> changePassword(@PathVariable String id, @RequestBody PasswordChangeRequest request) {
        try {
            userService.changePassword(id, request);
            return new ResponseEntity<>("Password has been successfully updated.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Send Email To Reset User's Password
     *
     * @param request
     * @return
     */
    @PostMapping("forgot-password")
    @Operation(
            summary = "Request Password Reset",
            description = "Sends a password reset link to the user's email address."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset email sent"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> forgotPasswordRequest(@RequestBody PasswordForgotRequest request) {
        try {
            userService.forgotPasswordRequest(request.getEmail());
            return new ResponseEntity<>("A link to reset your password has been sent to your email: " + request.getEmail(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Reset User's Password
     *
     * @param token
     * @param request
     * @return
     */
    @PatchMapping("reset-password/{token}")
    @Operation(
            summary = "Reset User Password",
            description = "Resets the password of a user using a valid reset token."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> resetPassword(@PathVariable String token, @RequestBody PasswordResetRequest request) {
        try {
            userService.resetPassword(token, request);
            return new ResponseEntity<>("Password has been successfully reset.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PatchMapping("{id}/authority")
    public ResponseEntity<UserDTO> updateAuthority(@PathVariable String id, @RequestBody AuthorityRequest request) {
        return new ResponseEntity<>(userService.updateAuthority(id, request), HttpStatus.OK);
    }
}
