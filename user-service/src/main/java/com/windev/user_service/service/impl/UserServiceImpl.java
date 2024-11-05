package com.windev.user_service.service.impl;

import com.windev.user_service.dto.UserDTO;
import com.windev.user_service.mapper.UserMapper;
import com.windev.user_service.model.User;
import com.windev.user_service.payload.request.PasswordChangeRequest;
import com.windev.user_service.payload.request.PasswordResetRequest;
import com.windev.user_service.payload.request.UpdateUserRequest;
import com.windev.user_service.payload.response.PaginatedResponse;
import com.windev.user_service.repository.UserRepository;
import com.windev.user_service.service.KafkaService;
import com.windev.user_service.service.UserService;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    private final KafkaService kafkaService;

    @Override
    public PaginatedResponse<UserDTO> getAllUsers(Pageable pageable) {
        Page<User> pageUser = userRepository.findAll(pageable);

        List<UserDTO> list = pageUser.getContent().stream().map(userMapper::toUserDTO).toList();

        return new PaginatedResponse<UserDTO>(list,
                pageUser.getNumber(),
                pageUser.getSize(),
                pageUser.isLast(),
                pageUser.getTotalPages(),
                pageUser.getTotalElements());
    }

    @Override
    public UserDTO getUserById(String id) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User with ID: " + id + " not found."));

        log.info("getUserById() --> user detail: {}", existingUser);
        return userMapper.toUserDTO(existingUser);
    }

    @Override
    public UserDTO updateUser(String id, UpdateUserRequest request) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User with ID: " + id + " not found."));

        userMapper.updateUserFromRequest(request, existingUser);

        User updatedUser = userRepository.save(existingUser);
        log.info("updateUser() --> user with id: {} successfully updated: {}", id, updatedUser);

        return userMapper.toUserDTO(existingUser);
    }

    @Override
    public void changePassword(String id, PasswordChangeRequest request) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User with ID: " + id + " not found."));

        /**
         * check trùng oldPassword - storedPassword
         */

        if(!passwordEncoder.matches(request.getOldPassword(), existingUser.getPassword())){
            log.warn("changePassword() --> old password does not match stored password");
            return;
        }

        /**
         * check trùng newPassword - storedPassword
         */
        if(!request.getNewPassword().equals(request.getConfirmNewPassword())){
            log.warn("changePassword() --> newPassword must be equals confirm new password");
            return;
        }

        existingUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        existingUser.setUpdatedAt(new Date());
        userRepository.save(existingUser);
        log.info("changePassword() --> password changed successfully!");
    }

    @Override
    public void forgotPasswordRequest(String email) {

        log.info("forgotPasswordRequest() --> a link to reset your password has been sent to your email: {}", email);
    }

    @Override
    public void resetPassword(String token, PasswordResetRequest request) {
        log.info("resetPassword() --> changed password successfully");
    }
}
