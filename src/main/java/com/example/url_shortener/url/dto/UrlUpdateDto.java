package com.example.url_shortener.url.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
@SuppressWarnings("ClassCanBeRecord")
public class UrlUpdateDto {
    @NotBlank(message = "Новий URL не може бути порожнім")
    String newUrl;
}
