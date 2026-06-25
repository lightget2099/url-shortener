package com.example.url_shortener.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Value
@SuppressWarnings("ClassCanBeRecord")
public class ErrorResponse {
    @Schema(description = "Повідомлення про помилку",
    example = "For shorten url: 'exampleURL' original URL was not found")
    String message;
    @Schema(description = "Точний час помилки",
    example = "2026-06-26T01:23:07.12207")
    LocalDateTime timestamp;
    @Schema(description = "Код помилки",
            example = "404 NOT_FOUND")
    HttpStatus status;
}
