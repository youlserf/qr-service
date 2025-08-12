package com.example.qr.dto;

import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class RegisterRequest {
    private String username;
    private String password;
}
