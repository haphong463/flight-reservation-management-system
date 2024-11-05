package com.windev.user_service.controller;

import com.windev.user_service.payload.request.PasswordChangeRequest;
import com.windev.user_service.payload.request.PasswordForgotRequest;
import com.windev.user_service.payload.request.PasswordResetRequest;
import com.windev.user_service.payload.request.UpdateUserRequest;
import com.windev.user_service.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/users")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Update User
     * @param id
     * @param request
     * @return
     */
    @PatchMapping("{id}")
    public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody UpdateUserRequest request) {
        try {
            return new ResponseEntity<>(userService.updateUser(id, request), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * GET All Users
     * @param pageable
     * @return
     */
    @GetMapping
    public ResponseEntity<?> getAllUsers(Pageable pageable) {
        try {
            return new ResponseEntity<>(userService.getAllUsers(pageable), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get User By Id
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public ResponseEntity<?> getUserById(@PathVariable String id) {
        try {
            return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Change User's Password
     * @param id
     * @param request
     * @return
     */
    @PatchMapping("{id}/change-password")
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
     * @param request
     * @return
     */
    @PostMapping("forgot-password")
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
     * @param token
     * @param request
     * @return
     */
    @PatchMapping("reset-password/{token}")
    public ResponseEntity<?> resetPassword(@PathVariable String token, @RequestBody PasswordResetRequest request) {
        try {
            userService.resetPassword(token, request);
            return new ResponseEntity<>("Password has been successfully reset.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
