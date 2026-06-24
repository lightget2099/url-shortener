package com.example.url_shortener.url.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;
import org.hibernate.validator.constraints.URL;

@Value
public class UrlRequestDto {

    @NotBlank(message = "URL can`t be empty")
    @URL(message = "Incorret URL(must starts with http:// or https:// ")
    String url;
}
