package com.windev.user_service.service;

import com.windev.user_service.dto.UserDTO;
import com.windev.user_service.payload.request.password.PasswordChangeRequest;
import com.windev.user_service.payload.request.password.PasswordResetRequest;
import com.windev.user_service.payload.request.user.AuthorityRequest;
import com.windev.user_service.payload.request.user_profile.UserProfileRequest;
import com.windev.user_service.payload.response.PaginatedResponse;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Pageable;

public interface UserService {
    PaginatedResponse<UserDTO> getAllUsers(Pageable pageable);

    List<UserDTO> findUserByIds(Set<String> userIds);

    UserDTO getUserById(String id);

    UserDTO updateUser(String id, UserProfileRequest request);

    void changePassword(String id, PasswordChangeRequest request);

    void forgotPasswordRequest(String email);

    void resetPassword(String token, PasswordResetRequest request);

    UserDTO updateAuthority(String id, AuthorityRequest request);
}
