package com.example.url_shortener.url.service;

import com.example.url_shortener.exception.UrlExpiredException;
import com.example.url_shortener.exception.UrlNotFoundException;
import com.example.url_shortener.exception.UserNotFoundException;
import com.example.url_shortener.url.dto.UrlRequestDto;
import com.example.url_shortener.url.dto.UrlStatsResponseDto;
import com.example.url_shortener.url.entity.UrlEntity;
import com.example.url_shortener.url.mapper.UrlMapper;
import com.example.url_shortener.url.repository.UrlRepository;
import com.example.url_shortener.user.entity.UserEntity;
import com.example.url_shortener.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final UserRepository userRepository;
    private final UrlMapper urlMapper;

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private static final String URL_PREFIX_ERROR = "For shorten url: '";
    private static final String NOT_FOUND_SUFFIX = "' original URL was not found";
    private static final String USER_PREFIX_ERROR = "User with username '";
    private static final String NOT_EXIST_SUFFIX = "' doesn't exist";

    private String encodeToBase62(long id) {
        StringBuilder sb = new StringBuilder();
        while (id > 0) {
            int remainder = (int) (id % 62);
            sb.append(ALPHABET.charAt(remainder));
            id = id / 62;
        }

        ThreadLocalRandom rand = ThreadLocalRandom.current();
        while (sb.length() < 6) {
            char randomChar = ALPHABET.charAt(rand.nextInt(ALPHABET.length()));
            sb.append(randomChar);
        }

        return sb.toString();
    }

    public String shortenUrl(UrlRequestDto dto, String username) {
        UserEntity user = userRepository.findByUsername(username).
                orElseThrow(() -> new UserNotFoundException(USER_PREFIX_ERROR + username + NOT_EXIST_SUFFIX));

        Long userId = user.getId();
        Optional<UrlEntity> existingUrl = urlRepository.findByUserIdAndUrl(userId, dto.getUrl());

        if (existingUrl.isPresent()) {
            return existingUrl.get().getCode();
        }

        UrlEntity urlEntity = new UrlEntity();
        urlEntity.setUrl(dto.getUrl());
        urlEntity.setCreatedAt(LocalDateTime.now());
        urlEntity.setExpiresAt(LocalDateTime.now().plusDays(30));

        if (dto.getExpirationDays() != null) {
            urlEntity.setExpiresAt(LocalDateTime.now().plusDays(dto.getExpirationDays()));
        } else {
            urlEntity.setExpiresAt(LocalDateTime.now().plusDays(30));
        }

        urlEntity.setUser(user);
        urlEntity.setCode("");
        UrlEntity savedUrl = urlRepository.save(urlEntity);

        String code = encodeToBase62(savedUrl.getId());
        savedUrl.setCode(code);
        urlRepository.save(savedUrl);

        return code;
    }

    @Transactional
    public String getOriginalUrl(String code) {
        UrlEntity urlEntity = urlRepository.findByCode(code)
                        .orElseThrow(() -> new UrlNotFoundException(URL_PREFIX_ERROR + code + NOT_FOUND_SUFFIX));

        if (urlEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new UrlExpiredException(URL_PREFIX_ERROR + code + " has expired");
        }

        urlRepository.incrementClickCount(code);
        return urlEntity.getUrl();
        }

        public UrlStatsResponseDto getUrlStats(String code) {
        UrlEntity urlEntity = urlRepository.findByCode(code).
                orElseThrow(() -> new UrlNotFoundException(URL_PREFIX_ERROR + code + NOT_FOUND_SUFFIX));

        return urlMapper.toStatsDto(urlEntity);
        }

        public List<UrlStatsResponseDto> getUserUrls(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(USER_PREFIX_ERROR + username + NOT_EXIST_SUFFIX));

            return urlMapper.toStatsDtoList(urlRepository.findByUserId(user.getId()));
        }

        public void deleteUrl(String code, String username) {
        UrlEntity urlEntity = urlRepository.findByCode(code).
                orElseThrow(()-> new UrlNotFoundException(URL_PREFIX_ERROR + code + NOT_FOUND_SUFFIX));

        if (!urlEntity.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("Ви не маєте права на видалення цього посилання");
        }

        urlRepository.delete(urlEntity);
        }


    public List<UrlStatsResponseDto> getActiveUserUrls(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(USER_PREFIX_ERROR + username + NOT_EXIST_SUFFIX));

        List<UrlEntity> activeUrls = urlRepository.findActiveByUserId(user.getId(), LocalDateTime.now());

        return urlMapper.toStatsDtoList(activeUrls);
    }

    @Transactional
    public UrlStatsResponseDto updateOriginalUrl(String code, String newUrl, String username) {
        UrlEntity urlEntity = urlRepository.findByCode(code)
                .orElseThrow(() -> new UrlNotFoundException(URL_PREFIX_ERROR + code + NOT_FOUND_SUFFIX));

        if (!urlEntity.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("Ви не маєте права на редагування цього посилання");
        }

        urlEntity.setUrl(newUrl);
        UrlEntity updated = urlRepository.save(urlEntity);

        return urlMapper.toStatsDto(updated);
    }
    }
