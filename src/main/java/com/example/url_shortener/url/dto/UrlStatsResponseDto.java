package com.example.url_shortener.url.dto;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class UrlStatsResponseDto {
    String url;
    String code;
    Integer clicks;
    LocalDateTime createdAt;
    LocalDateTime expiresAt;
}
