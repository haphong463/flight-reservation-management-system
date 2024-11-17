package com.windev.user_service.service.impl;

import com.windev.user_service.dto.UserDTO;
import com.windev.user_service.dto.UserProfileDTO;
import com.windev.user_service.enums.EventType;
import com.windev.user_service.exception.AuthorityNotFoundException;
import com.windev.user_service.exception.UserNotFoundException;
import com.windev.user_service.mapper.UserMapper;
import com.windev.user_service.model.Authority;
import com.windev.user_service.model.ForgotPasswordToken;
import com.windev.user_service.model.User;
import com.windev.user_service.payload.request.password.PasswordChangeRequest;
import com.windev.user_service.payload.request.password.PasswordResetRequest;
import com.windev.user_service.payload.request.user.AuthorityRequest;
import com.windev.user_service.payload.request.user_profile.UserProfileRequest;
import com.windev.user_service.payload.response.PaginatedResponse;
import com.windev.user_service.repository.AuthorityRepository;
import com.windev.user_service.repository.ForgotPasswordTokenRepository;
import com.windev.user_service.repository.UserRepository;
import com.windev.user_service.service.KafkaService;
import com.windev.user_service.service.UserProfileService;
import com.windev.user_service.service.UserService;
import java.security.SecureRandom;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
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

    private final UserProfileService userProfileService;

    private final AuthorityRepository authorityRepository;

    @Override
    public PaginatedResponse<UserDTO> getAllUsers(Pageable pageable) {
        Page<User> pageUser = userRepository.findAll(pageable);

        List<UserDTO> list = pageUser.getContent().stream().map(user -> {
            UserDTO userDTO = userMapper.toUserDTO(user);

            // Retrieve the UserProfile for each user and set it in the UserDTO
            UserProfileDTO userProfileDTO = userProfileService.getUserProfile(user.getId());
            userDTO.setProfile(userProfileDTO);

            return userDTO;
        }).toList();


        return new PaginatedResponse<UserDTO>(list,
                pageUser.getNumber(),
                pageUser.getSize(),
                pageUser.isLast(),
                pageUser.getTotalPages(),
                pageUser.getTotalElements());
    }

    @Override
    public List<UserDTO> findUserByIds(Set<String> userIds) {
        return userRepository.findAllByIdIn(userIds).stream().map(user -> {
            UserDTO userDTO = userMapper.toUserDTO(user);
            UserProfileDTO userProfileDTO = userProfileService.getUserProfile(user.getId());
            userDTO.setProfile(userProfileDTO);

            return userDTO;
        }).toList();
    }

    @Override
    public UserDTO getUserById(String id) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User with ID: " + id + " not found."));

        UserProfileDTO userProfileDTO = userProfileService.getUserProfile(id);

        UserDTO result = userMapper.toUserDTO(existingUser);
        log.info("getUserById() --> user detail: {}", existingUser);
        result.setProfile(userProfileDTO);
        return result;
    }

    @Override
    public UserDTO updateUser(String id, UserProfileRequest request) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User with ID: " + id + " not found."));

//        userMapper.updateUserFromRequest(request, existingUser);
        UserProfileDTO userProfileDTO = userProfileService.updateUserProfile(id, request);


//        User updatedUser = userRepository.save(existingUser);
//        log.info("updateUser() --> user with id: {} successfully updated: {}", id, updatedUser);

        UserDTO result = userMapper.toUserDTO(existingUser);
        result.setProfile(userProfileDTO);
        return result;
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

        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
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

    @Override
    public UserDTO updateAuthority(String id, AuthorityRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id: " + id + " not found"));

        List<Authority> authorities = request.getNames().stream()
                .map(name -> authorityRepository.findByName(name)
                        .orElseThrow(() -> new AuthorityNotFoundException("Authority with name: " + name + " not " +
                                "found."))).toList();


        user.setAuthorities(authorities);
        User result = userRepository.save(user);

        return userMapper.toUserDTO(result);
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
