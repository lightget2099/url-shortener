package com.example.url_shortener.url;

import com.example.url_shortener.user.UserEntity;
import com.example.url_shortener.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final UserRepository userRepository;

    private String encodeToBase62(long id) {
        java.util.Random rand = new java.util.Random();
        String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        while (id > 0) {
            int remainder = (int) (id % 62);
            sb.append(alphabet.charAt(remainder));
            id = id / 62;
        }

        while (sb.length() < 6) {
            char randomChar = alphabet.charAt(rand.nextInt(alphabet.length()));
            sb.append(randomChar);
        }

        return sb.toString();
    }

    public String shortenUrl(String url, Long userId) {
        UrlEntity urlEntity = new UrlEntity();
        urlEntity.setUrl(url);
        urlEntity.setCreatedAt(LocalDateTime.now());
        UserEntity user = userRepository.findById(userId).
                orElseThrow(() -> new RuntimeException("User not found"));
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
                orElseThrow(() -> new RuntimeException("URL not found"));
        urlEntity.setClickCount(urlEntity.getClickCount() + 1);
        urlRepository.save(urlEntity);
        return urlEntity.getUrl();
        }
    }
