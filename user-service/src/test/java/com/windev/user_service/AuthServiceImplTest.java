package com.windev.user_service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.windev.user_service.mapper.UserMapper;
import com.windev.user_service.model.EmailVerificationToken;
import com.windev.user_service.model.Role;
import com.windev.user_service.model.User;
import com.windev.user_service.payload.request.auth.SignupRequest;
import com.windev.user_service.payload.response.UserRegisteredResponse;
import com.windev.user_service.repository.EmailVerificationTokenRepository;
import com.windev.user_service.repository.RoleRepository;
import com.windev.user_service.repository.UserRepository;
import com.windev.user_service.service.KafkaService;
import com.windev.user_service.service.impl.AuthServiceImpl;
import java.util.List;
import java.util.Optional;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private KafkaService kafkaService;

    @Mock
    private EmailVerificationTokenRepository emailVerificationTokenRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    public void testRegister_Success(){
        SignupRequest req = new SignupRequest();
        req.setUsername("newUser");
        req.setEmail("newUser@example.com");
        req.setPassword("password123");

        when(userRepository.findByUsername("newUser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("newUser@example.com")).thenReturn(Optional.empty());

        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        Role role = new Role();
        role.setId("roleId123");
        role.setName("ROLE_CUSTOMER");
        when(roleRepository.findByName("ROLE_CUSTOMER")).thenReturn(Optional.of(role));

        User user = User.builder()
                .id("userId123")
                .email("newUser@example.com")
                .username("newUser")
                .password("encodedPassword")
                .roles(List.of(role))
                .createdAt(new Date())
                .updatedAt(new Date())
                .build();
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserRegisteredResponse response = new UserRegisteredResponse();
        response.setId("userId123");
        response.setEmail("newUser@example.com");
        response.setUsername("newUser");
        when(userMapper.toUserRegisteredResponse(user)).thenReturn(response);

        when(emailVerificationTokenRepository.save(any(EmailVerificationToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserRegisteredResponse result = authService.register(req);

        assertNotNull(result);
        assertEquals("userId123", result.getId());
        assertEquals("newUser@example.com", result.getEmail());
        assertEquals("newUser", result.getUsername());

        verify(userRepository, times(1)).findByUsername("newUser");
        verify(userRepository, times(1)).findByEmail("newUser@example.com");
        verify(passwordEncoder, times(1)).encode("password123");
        verify(roleRepository, times(1)).findByName("ROLE_CUSTOMER");
        verify(userRepository, times(1)).save(any(User.class));
        verify(emailVerificationTokenRepository, times(1)).save(any(EmailVerificationToken.class));
        verify(kafkaService, times(1)).sendMessage(user, "USER_REGISTERED");
        verify(userMapper, times(1)).toUserRegisteredResponse(user);
    }
}
