package com.windev.user_service.service.impl;

import com.windev.user_service.model.Role;
import com.windev.user_service.model.User;
import com.windev.user_service.payload.request.SigninRequest;
import com.windev.user_service.payload.request.SignupRequest;
import com.windev.user_service.repository.RoleRepository;
import com.windev.user_service.repository.UserRepository;
import com.windev.user_service.security.JwtTokenProvider;
import com.windev.user_service.service.AuthService;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public User register(SignupRequest req) {

        if (userRepository.findByUsername(req.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists.");
        }

        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists.");
        }

        String passwordEncoded = passwordEncoder.encode(req.getPassword());

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("ROLE_USER not found"));

        User user = User.builder()
                .email(req.getEmail())
                .username(req.getUsername())
                .password(passwordEncoded)
                .roles(List.of(userRole))
                .createdAt(new Date())
                .updatedAt(new Date())
                .preferences(req.getPreferences())
                .build();

        User createdUser = userRepository.save(user);
        log.info("register() --> user has been successfully registered: {}", createdUser);
        return createdUser;
    }

    @Override
    public String login(SigninRequest req) {
//        Optional<User> userOpt = userRepository.findByUsername(req.getUsername());
//        if (userOpt.isPresent()) {
//            User user = userOpt.get();
//            if (passwordEncoder.matches(req.getPassword(), user.getPassword())) {
//                return Optional.of(user);
//            }
//        }
//        return Optional.empty();
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.getUsername(),
                        req.getPassword()
                )
        );

        // Nếu xác thực thành công, tạo JWT
        String token = jwtTokenProvider.generateToken(authentication);
        log.info("authenticate() --> user {} authenticated successfully", req.getUsername());
        return token;
    }
}
