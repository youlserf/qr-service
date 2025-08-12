package com.example.qr.service;

import com.example.qr.dto.AuthRequest;
import com.example.qr.dto.AuthResponse;
import com.example.qr.dto.RegisterRequest;

public interface AuthService {
    void register(RegisterRequest request);
    AuthResponse login(AuthRequest request);
}
