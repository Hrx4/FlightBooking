package com.example.AuthService.Dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponseDto {

    private String token;

    private String userId;

    private String email;

    private String role;
}