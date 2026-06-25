package com.example.url_shortener.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class UserLoginRequestDto {
    @NotBlank
    String username;
    @NotBlank
    String password;
}
