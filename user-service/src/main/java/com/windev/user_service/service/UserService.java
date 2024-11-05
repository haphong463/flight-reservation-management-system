package com.windev.user_service.service;

import com.windev.user_service.dto.UserDTO;
import com.windev.user_service.payload.request.PasswordChangeRequest;
import com.windev.user_service.payload.request.PasswordResetRequest;
import com.windev.user_service.payload.request.UpdateUserRequest;
import com.windev.user_service.payload.response.PaginatedResponse;
import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    PaginatedResponse<UserDTO> getAllUsers(Pageable pageable);
    UserDTO getUserById(String id);
    UserDTO updateUser(String id, UpdateUserRequest request);
    void changePassword(String id, PasswordChangeRequest request);
    void forgotPasswordRequest(String email);
    void resetPassword(String token, PasswordResetRequest request);
}
