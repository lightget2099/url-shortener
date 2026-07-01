package com.example.url_shortener.url.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
@SuppressWarnings("ClassCanBeRecord")
public class UrlUpdateDto {
    @NotBlank(message = "Новий URL не може бути порожнім")
    @Schema(description = "Новий URL для заміни старого",
            example = "https://github.com")
    String newUrl;
}
