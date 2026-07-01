package com.example.url_shortener.url.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Value;
import org.hibernate.validator.constraints.URL;

@Value
@SuppressWarnings("ClassCanBeRecord")
public class UrlRequestDto {
    @NotBlank(message = "URL can`t be empty")
    @URL(message = "Incorret URL(must starts with http:// or https:// ")
            @Schema(description = "Довгий URL",
            example = "https://github.com")
    String url;

    @Positive(message = "Кількість днів має бути більшою за 0")
    @Schema(description = "Опціональна кількість днів життя посилання (дефолт 30 днів)",
            example = "7", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    Integer expirationDays;
}
