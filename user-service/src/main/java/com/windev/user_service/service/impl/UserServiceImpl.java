package com.windev.user_service.service.impl;

import com.windev.user_service.dto.UserDTO;
import com.windev.user_service.enums.EventType;
import com.windev.user_service.mapper.UserMapper;
import com.windev.user_service.model.ForgotPasswordToken;
import com.windev.user_service.model.User;
import com.windev.user_service.payload.request.PasswordChangeRequest;
import com.windev.user_service.payload.request.PasswordResetRequest;
import com.windev.user_service.payload.request.UpdateUserRequest;
import com.windev.user_service.payload.response.PaginatedResponse;
import com.windev.user_service.repository.ForgotPasswordTokenRepository;
import com.windev.user_service.repository.UserRepository;
import com.windev.user_service.service.KafkaService;
import com.windev.user_service.service.UserService;
import java.security.SecureRandom;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Hex;
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

    private final ForgotPasswordTokenRepository forgotPasswordTokenRepository;

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

        if (!passwordEncoder.matches(request.getOldPassword(), existingUser.getPassword())) {
            log.warn("changePassword() --> old password does not match stored password");
            return;
        }

        /**
         * check trùng newPassword - storedPassword
         */
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
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
        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User with email: " + email + " not found."));

        String token = generateEmailVerificationToken();

        ForgotPasswordToken forgotPasswordToken = new ForgotPasswordToken();
        forgotPasswordToken.setUserId(existingUser.getId());
        forgotPasswordToken.setToken(token);

        forgotPasswordTokenRepository.save(forgotPasswordToken);

        kafkaService.sendMessage(existingUser, EventType.FORGOT_PASSWORD.name());

        log.info("forgotPasswordRequest() --> a link to reset your password has been sent to your email: {}", email);
    }

    @Override
    public void resetPassword(String token, PasswordResetRequest request) {
        ForgotPasswordToken existingToken = forgotPasswordTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token!"));

        if (existingToken.isUsed()) {
            throw new RuntimeException("Token has already been used!");
        }

        if (existingToken.getExpiresAt().before(new Date())) {
            throw new RuntimeException("Token has expired!");
        }

        if(!request.getNewPassword().equals(request.getConfirmNewPassword())){
            throw new RuntimeException("Password doesn't match confirm password");
        }

        existingToken.setUsed(true);
        forgotPasswordTokenRepository.save(existingToken);


        String userId = existingToken.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found for this token"));
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("resetPassword() --> changed password successfully");
    }

    /**
     * Generate a secure email verification token
     *
     * @return token
     */
    private String generateEmailVerificationToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] tokenBytes = new byte[16];
        secureRandom.nextBytes(tokenBytes);
        return Hex.toHexString(tokenBytes);
    }
}
