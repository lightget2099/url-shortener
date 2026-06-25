package com.example.url_shortener.url.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

@Value
@SuppressWarnings("ClassCanBeRecord")
public class UrlResponseDto {
    @Schema(description = "Короткий згенерованний код від URL (6-8 символів)",
    example = "gFFfVO")
    String code;
}
