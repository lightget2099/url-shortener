package com.example.url_shortener.url.controller;

import com.example.url_shortener.url.dto.UrlRequestDto;
import com.example.url_shortener.url.dto.UrlResponseDto;
import com.example.url_shortener.url.dto.UrlStatsResponseDto;
import com.example.url_shortener.url.service.UrlService;
import com.example.url_shortener.user.repository.UserRepository;
import com.example.url_shortener.user.service.CustomUserDetailsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/urls")
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @PostMapping
    public ResponseEntity<UrlResponseDto> createShortenUrl(@Valid @RequestBody UrlRequestDto dto,
                                                           @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        UrlResponseDto response = new UrlResponseDto(urlService.shortenUrl(dto.getUrl(), username));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{code}")
    public ResponseEntity<String> getOriginalUrl(@PathVariable String code) {
        String originalUrl = urlService.getOriginalUrl(code);
        java.net.URI redirectUri = java.net.URI.create(originalUrl);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(redirectUri)
                .build();
    }

    @GetMapping("/{code}/stats")
    public UrlStatsResponseDto getUrlStats(@PathVariable String code) {
        return urlService.getUrlStats(code);
    }

    @DeleteMapping("/{code}")
    public void deleteUrl(@PathVariable String code) {
        urlService.deleteUrl(code);
    }

}