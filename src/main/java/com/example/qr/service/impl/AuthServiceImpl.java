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
import com.example.qr.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j // Anotación de Lombok para tener un logger SLF4J
public class AuthServiceImpl implements AuthService {

    // Constantes para mensajes de error
    private static final String USER_NOT_FOUND_MSG = "Usuario no encontrado: ";
    private static final String USER_ALREADY_EXISTS_MSG = "El nombre de usuario ya está en uso: ";
    private static final String INVALID_CREDENTIALS_MSG = "Credenciales inválidas.";

    // Dependencias finales inyectadas por constructor gracias a @RequiredArgsConstructor
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public void register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException(USER_ALREADY_EXISTS_MSG + request.getUsername());
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(Set.of("ROLE_USER"));

        userRepository.save(user);
        log.info("Usuario registrado exitosamente: {}", request.getUsername());
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MSG + request.getUsername()));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Intento de login fallido para el usuario: {}", request.getUsername());
            throw new InvalidCredentialsException(INVALID_CREDENTIALS_MSG);
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRoles());
        log.info("Login exitoso para el usuario: {}", request.getUsername());
        return new AuthResponse(token);
    }
}