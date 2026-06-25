package com.example.url_shortener.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

@Value
public class UserLoginResponseDto {
    @Schema(description = "JWT токен",
    example = "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJ0ZXN0VXNlciIsImlhdCI6MTc4MjQyNjc1NiwiZXhwIjoxNzgyNTEzMTU2fQ." +
            "1tQ8Tuwem5IYzPezWNxJ6y3MzHMA8uczF0OBNmOcuFZjIyCKmkbxL77gtglFLiJB")
    String token;
}
