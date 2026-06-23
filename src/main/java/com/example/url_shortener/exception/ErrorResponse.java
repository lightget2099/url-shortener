package com.example.url_shortener.exception;

import lombok.Value;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Value
public class ErrorResponse {
    String message;
    LocalDateTime timestamp;
    HttpStatus status;
}
