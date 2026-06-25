package com.example.url_shortener.url.service;

import com.example.url_shortener.url.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UrlCleanUpSchedulerService {
    private final UrlRepository urlRepository;

    @Scheduled(cron = "0 0 1 * * *")
    public void cleanUpExpiredUrls() {
        urlRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }
}
