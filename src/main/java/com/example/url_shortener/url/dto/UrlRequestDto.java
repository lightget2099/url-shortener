package com.example.url_shortener.url.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
}
