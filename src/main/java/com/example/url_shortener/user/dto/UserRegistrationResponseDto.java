package com.example.url_shortener.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

@Value
@SuppressWarnings("ClassCanBeRecord")
public class UserRegistrationResponseDto {
    @Schema(description = "ID користувача",
    example = "5")
    Long id;
    @Schema(description = "Ім`я користувача",
            example = "Bohdan_dev")
    String username;
}
