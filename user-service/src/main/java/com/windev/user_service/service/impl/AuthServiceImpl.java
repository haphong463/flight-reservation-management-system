package com.windev.user_service.service.impl;

import com.windev.user_service.dto.UserDTO;
import com.windev.user_service.dto.UserProfileDTO;
import com.windev.user_service.enums.EventType;
import com.windev.user_service.mapper.UserMapper;
import com.windev.user_service.model.*;
import com.windev.user_service.payload.request.auth.SigninRequest;
import com.windev.user_service.payload.request.auth.SignupRequest;
import com.windev.user_service.payload.response.JwtResponse;
import com.windev.user_service.payload.response.UserRegisteredResponse;
import com.windev.user_service.repository.BlacklistedTokenRepository;
import com.windev.user_service.repository.EmailVerificationTokenRepository;
import com.windev.user_service.repository.RoleRepository;
import com.windev.user_service.repository.UserRepository;
import com.windev.user_service.security.JwtTokenProvider;
import com.windev.user_service.service.AuthService;
import com.windev.user_service.service.KafkaService;
import com.windev.user_service.service.UserProfileService;
import java.security.SecureRandom;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    private final UserMapper userMapper;

    private final BlacklistedTokenRepository blacklistedTokenRepository;

    private final KafkaService kafkaService;

    private final EmailVerificationTokenRepository emailVerificationTokenRepository;

    private final UserProfileService userProfileService;


    /**
     * Register An User
     *
     * @param req
     * @return
     */
    @Override
    @Transactional
    public UserRegisteredResponse register(SignupRequest req) {
        if (userRepository.findByUsername(req.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists.");
        }

        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists.");
        }

        String token = generateEmailVerificationToken();

        String passwordEncoded = passwordEncoder.encode(req.getPassword());

        Role userRole = roleRepository.findByName("ROLE_CUSTOMER")
                .orElseThrow(() -> new RuntimeException("ROLE_CUSTOMER not found"));

        User user = User.builder()
                .email(req.getEmail())
                .username(req.getUsername())
                .password(passwordEncoded)
                .roles(List.of(userRole))
                .preferences(req.getPreferences())
                .build();

        User createdUser = userRepository.save(user);
        log.info("register() --> user has been successfully registered: {}", createdUser);

        userProfileService.createUserProfile(createdUser.getId(), req.getUserProfileRequest());

        /**
         * Store token into Email Verification Token schema
         */
        EmailVerificationToken emailVerificationToken = new EmailVerificationToken();
        emailVerificationToken.setToken(token);
        emailVerificationToken.setUserId(createdUser.getId());
        emailVerificationTokenRepository.save(emailVerificationToken);
        log.info("Email verification token has been saved for user: {}", createdUser.getUsername());


        /**
         * Send user-registered event to notification topic
         */
        kafkaService.sendMessage(createdUser, EventType.USER_REGISTERED.name());

        return userMapper.toUserRegisteredResponse(createdUser);
    }


    /**
     * Login User - Response JWT Token
     *
     * @param req
     * @return
     */
    @Override
    public JwtResponse login(SigninRequest req) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.getUsername(),
                        req.getPassword()
                )
        );

        String token = jwtTokenProvider.generateToken(authentication);
        log.info("authenticate() --> user {} authenticated successfully", req.getUsername());
        return new JwtResponse(token);
    }


    /**
     * Get Current User By JWT Token
     *
     * @return
     */
    @Override
    public UserDTO currentUser(String token) {
        String username = jwtTokenProvider.getUsernameFromJWT(token);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Username not found"));

        UserProfileDTO userProfile = userProfileService.getUserProfile(user.getId());

        UserDTO userDTO = userMapper.toUserDTO(user);

        userDTO.setProfile(userProfile);

        return userDTO;
    }

    /**
     * Log out
     *
     * @param token
     */
    @Override
    public void logout(String token) {
        long expiration = jwtTokenProvider.getExpirationDuration(token);
        if (expiration > 0) {
            Date expiryDate = new Date(System.currentTimeMillis() + expiration);
            BlacklistedToken blacklistedToken = new BlacklistedToken(token, expiryDate);
            blacklistedTokenRepository.save(blacklistedToken);
        }
    }

    /**
     * Verify email
     *
     * @param token
     */
    @Override
    public void verifyEmail(String token) {
        EmailVerificationToken emailVerificationToken = emailVerificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token!!!"));

        /**
         * Check token is used
         */
        if (emailVerificationToken.isUsed()) {
            throw new RuntimeException("Token has already been used!");
        }

        /**
         * Check token expiration
         */
        if (emailVerificationToken.getExpiresAt().before(new Date())) {
            throw new RuntimeException("Token has expired!");
        }

        emailVerificationToken.setUsed(true);
        emailVerificationTokenRepository.save(emailVerificationToken);

        String userId = emailVerificationToken.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found for this token"));

        user.setEmailVerified(true);
        userRepository.save(user);

        log.info("verifyEmail() --> verify email ok");
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
