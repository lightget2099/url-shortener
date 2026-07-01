package com.example.url_shortener.url.controller;

import com.example.url_shortener.url.dto.UrlRequestDto;
import com.example.url_shortener.url.dto.UrlResponseDto;
import com.example.url_shortener.url.dto.UrlStatsResponseDto;
import com.example.url_shortener.url.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/urls")
@RequiredArgsConstructor
@Tag(name = "Всі дії над посиланнями",
description = "Управління короткими лінками, редиректи та перегляд статистики")
public class UrlController {
    private final UrlService urlService;

    @PostMapping
    @Operation(summary = "Створення короткого посилання",
    description = "Генеруж випадковий код 6-8 символів для довгої URL адресси")
    public ResponseEntity<UrlResponseDto> createShortenUrl(@Valid @RequestBody UrlRequestDto dto,
                                                           @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        UrlResponseDto response = new UrlResponseDto(urlService.shortenUrl(dto.getUrl(), username));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/r/{code}")
    @Operation(summary = "Повернення довгого URL",
            description = "Повертає оригінальний URL за вказанним коротким кодом")
    public ResponseEntity<String> getOriginalUrl(@PathVariable String code) {
        String originalUrl = urlService.getOriginalUrl(code);
        java.net.URI redirectUri = java.net.URI.create(originalUrl);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(redirectUri)
                .build();
    }

    @GetMapping("/{code}/stats")
    @Operation(summary = "Інформація про певний URL",
    description = "Отримання всієї інформації про конктрений URL")
    public UrlStatsResponseDto getUrlStats(@PathVariable String code) {
        return urlService.getUrlStats(code);
    }

    @DeleteMapping("/{code}")
    @Operation(summary = "Видалення URL",
            description = "Видаляє URL за кодом")
    public void deleteUrl(@PathVariable String code, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        urlService.deleteUrl(code, username);
    }

}