package com.example.url_shortener.url.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@SuppressWarnings("ClassCanBeRecord")
public class UrlStatsResponseDto {
    @Schema(description = "Довгий URL",
            example = "https://github.com")
    String url;
    @Schema(description = "Короткий згенерованний код від URL",
            example = "gFFfVO")
    String code;
    @Schema(description = "Кількість кліків на посилання",
    example = "5")
    Integer clicks;
    @Schema(description = "Точний час створення скороченного URL",
            example = "2026-06-26T01:23:07.12207")
    LocalDateTime createdAt;
    @Schema(description = "Час закінчення терміну дії URL",
            example = "2026-07-26T01:23:07.12207")
    LocalDateTime expiresAt;
}
