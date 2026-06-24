package com.example.url_shortener.url.controller;

import com.example.url_shortener.url.dto.UrlRequestDto;
import com.example.url_shortener.url.dto.UrlResponseDto;
import com.example.url_shortener.url.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/urls")
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @PostMapping
    public ResponseEntity<UrlResponseDto> createShortenUrl(@RequestBody UrlRequestDto dto,
                                                           @RequestParam Long userId) {
        UrlResponseDto test = new UrlResponseDto(urlService.shortenUrl(dto.getUrl(), userId));
        return ResponseEntity.ok(test);
    }

    @GetMapping("/{code}")
    public ResponseEntity<?> getOriginalUrl(@PathVariable String code) {
        String originalUrl = urlService.getOriginalUrl(code);
        java.net.URI redirectUri = java.net.URI.create(originalUrl);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(redirectUri)
                .build();
    }
}