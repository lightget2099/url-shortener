package com.example.url_shortener.url.service;

import com.example.url_shortener.exception.UrlExpiredException;
import com.example.url_shortener.exception.UrlNotFoundException;
import com.example.url_shortener.exception.UserNotFoundException;
import com.example.url_shortener.url.dto.UrlStatsResponseDto;
import com.example.url_shortener.url.entity.UrlEntity;
import com.example.url_shortener.url.mapper.UrlMapper;
import com.example.url_shortener.url.repository.UrlRepository;
import com.example.url_shortener.user.entity.UserEntity;
import com.example.url_shortener.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
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
    private static final String USER_PREFIX_ERROR = "User with ID ";
    private static final String NOT_EXIST_SUFFIX = " doesn't exist";

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

    public String shortenUrl(String url, Long userId) {
        UrlEntity urlEntity = new UrlEntity();
        urlEntity.setUrl(url);
        urlEntity.setCreatedAt(LocalDateTime.now());
        urlEntity.setExpiresAt(LocalDateTime.now().plusDays(30));
        UserEntity user = userRepository.findById(userId).
                orElseThrow(() -> new UserNotFoundException(USER_PREFIX_ERROR + userId + NOT_EXIST_SUFFIX));
        urlEntity.setUser(user);
        urlEntity.setCode("");
        UrlEntity savedUrl = urlRepository.save(urlEntity);

        String code = encodeToBase62(savedUrl.getId());
        savedUrl.setCode(code);
        urlRepository.save(savedUrl);

        return code;
    }

    public String getOriginalUrl(String code) {
        UrlEntity urlEntity = urlRepository.findByCode(code).
                orElseThrow(() -> new UrlNotFoundException(URL_PREFIX_ERROR + code + NOT_FOUND_SUFFIX));

        if (urlEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new UrlExpiredException(URL_PREFIX_ERROR + code + " has expired");
        }

        urlEntity.setClickCount(urlEntity.getClickCount() + 1);
        urlRepository.save(urlEntity);
        return urlEntity.getUrl();
        }

        public UrlStatsResponseDto getUrlStats(String code) {
        UrlEntity urlEntity = urlRepository.findByCode(code).
                orElseThrow(() -> new UrlNotFoundException(URL_PREFIX_ERROR + code + NOT_FOUND_SUFFIX));

        return urlMapper.toStatsDto(urlEntity);
        }

        public List<UrlStatsResponseDto> getUserUrls(Long userId) {
            if (!userRepository.existsById(userId)) {
                throw new UserNotFoundException(USER_PREFIX_ERROR + userId + NOT_EXIST_SUFFIX);
            }

            return urlMapper.toStatsDtoList(urlRepository.findByUserId(userId));
        }

        public void deleteUrl(String code) {
        UrlEntity urlEntity = urlRepository.findByCode(code).
                orElseThrow(()-> new UrlNotFoundException(URL_PREFIX_ERROR + code + NOT_FOUND_SUFFIX));
        urlRepository.delete(urlEntity);
        }
    }
