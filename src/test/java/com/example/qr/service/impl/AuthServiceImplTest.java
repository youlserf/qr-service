package com.example.qr.service.impl;

import com.example.qr.dto.AuthRequest;
import com.example.qr.dto.AuthResponse;
import com.example.qr.dto.RegisterRequest;
import com.example.qr.entity.User;
import com.example.qr.exception.InvalidCredentialsException;
import com.example.qr.exception.ResourceNotFoundException;
import com.example.qr.exception.UserAlreadyExistsException;
import com.example.qr.repository.UserRepository;
import com.example.qr.security.JwtUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void register_Success() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("testuser");
        req.setPassword("password123");

        Mockito.when(userRepository.existsByUsername("testuser")).thenReturn(false);
        Mockito.when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        authService.register(req);

        Mockito.verify(userRepository).save(Mockito.argThat(user ->
                user.getUsername().equals("testuser") &&
                        user.getPassword().equals("encodedPassword") &&
                        user.getRoles().contains("ROLE_USER")
        ));
    }

    @Test
    void register_UserAlreadyExists_ThrowsException() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("existingUser");
        req.setPassword("pass");

        Mockito.when(userRepository.existsByUsername("existingUser")).thenReturn(true);

        Assertions.assertThrows(UserAlreadyExistsException.class, () -> {
            authService.register(req);
        });
    }

    @Test
    void login_Success() {
        AuthRequest req = new AuthRequest();
        req.setUsername("user");
        req.setPassword("pass");

        User user = new User();
        user.setUsername("user");
        user.setPassword("encodedPass");
        user.setRoles(Set.of("ROLE_USER"));

        Mockito.when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        Mockito.when(passwordEncoder.matches("pass", "encodedPass")).thenReturn(true);
        Mockito.when(jwtUtil.generateToken("user", user.getRoles())).thenReturn("token123");

        AuthResponse response = authService.login(req);

        Assertions.assertEquals("token123", response.getToken());
    }

    @Test
    void login_UserNotFound_ThrowsException() {
        AuthRequest req = new AuthRequest();
        req.setUsername("notfound");
        req.setPassword("pass");

        Mockito.when(userRepository.findByUsername("notfound")).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            authService.login(req);
        });
    }

    @Test
    void login_InvalidPassword_ThrowsException() {
        AuthRequest req = new AuthRequest();
        req.setUsername("user");
        req.setPassword("wrongpass");

        User user = new User();
        user.setUsername("user");
        user.setPassword("encodedPass");

        Mockito.when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        Mockito.when(passwordEncoder.matches("wrongpass", "encodedPass")).thenReturn(false);

        Assertions.assertThrows(InvalidCredentialsException.class, () -> {
            authService.login(req);
        });
    }
}
