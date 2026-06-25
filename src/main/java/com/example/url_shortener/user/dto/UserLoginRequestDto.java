package com.example.url_shortener.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
@SuppressWarnings("ClassCanBeRecord")
public class UserLoginRequestDto {
    @NotBlank
            @Schema(description = "Ім`я користувача",
            example = "Bohdan_dev")
    String username;
    @NotBlank
    @Schema(description = "Пароль користувача (мін. 8 символів, цифри, великі/малі літери)",
            example = "Qwerty1234")
    String password;
}
