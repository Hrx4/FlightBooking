package com.example.AuthService.Service;


import com.example.AuthService.Dto.AuthResponseDto;
import com.example.AuthService.Dto.LoginRequestDto;
import com.example.AuthService.Dto.RegisterRequestDto;

public interface AuthService {

    AuthResponseDto register(RegisterRequestDto request);

    AuthResponseDto login(LoginRequestDto request);
}