package com.example.url_shortener.url.controller;

import com.example.url_shortener.url.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RedirectController {
    private final UrlService urlService;

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
}