package com.example.url_shortener.url.controller;

import com.example.url_shortener.url.dto.UrlRequestDto;
import com.example.url_shortener.url.dto.UrlResponseDto;
import com.example.url_shortener.url.dto.UrlStatsResponseDto;
import com.example.url_shortener.url.dto.UrlUpdateDto;
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

import java.util.List;

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

        String code = urlService.shortenUrl(dto, username);
        String fullShortUrl = "http://localhost:8080/r/" + code;
        UrlResponseDto response = new UrlResponseDto(code, fullShortUrl);

        return ResponseEntity.ok(response);
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


    @GetMapping("/active")
    @Operation(summary = "Отримання тільки активних посилань",
            description = "Повертає список активних посилань користувача, термін дії яких ще не вичерпано")
    public ResponseEntity<List<UrlStatsResponseDto>> getActiveUrls(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        return ResponseEntity.ok(urlService.getActiveUserUrls(username));
    }


    @PatchMapping("/{code}")
    @Operation(summary = "Редагування оригінального URL",
            description = "Дозволяє змінити оригінальний довгий URL для існуючого короткого коду")
    public ResponseEntity<UrlStatsResponseDto> updateUrl(@PathVariable String code,
                                                         @Valid @RequestBody UrlUpdateDto dto,
                                                         @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        UrlStatsResponseDto response = urlService.updateOriginalUrl(code, dto.getNewUrl(), username);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Список всіх ваших URL",
            description = "Повертає абсолютно всі створені вами URL разом зі статистикою")
    public ResponseEntity<List<UrlStatsResponseDto>> getUserUrls(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        return ResponseEntity.ok(urlService.getUserUrls(username));
    }
}