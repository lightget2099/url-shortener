package com.example.url_shortener.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Value;

@Value
@SuppressWarnings("ClassCanBeRecord")
public class UserRegistrationRequestDto {
    @NotBlank
    @Schema(description = "Ім`я користувача",
            example = "Bohdan_dev")
    @Size(min = 3, max = 20,
            message = "Ім'я користувача має бути від 3 до 20 символів")
    String username;

    @NotBlank
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
            message = "Пароль має містити щонайменше 8 символів, включаючи одну велику літеру, одну малу літеру та одну цифру"
    )
    @Schema(description = "Пароль користувача (мін. 8 символів, цифри, великі/малі літери)",
            example = "Qwerty1234")
    String password;
}
