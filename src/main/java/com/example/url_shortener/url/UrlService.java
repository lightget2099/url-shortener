package com.example.url_shortener.url;

import com.example.url_shortener.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;

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

    public String shortenUrl(String url, UserEntity user) {
        UrlEntity urlEntity = new UrlEntity();
        urlEntity.setUrl(url);
        urlEntity.setCreatedAt(LocalDateTime.now());
        urlEntity.setUser(user);
        UrlEntity savedUrl = urlRepository.save(urlEntity);

        String code = encodeToBase62(savedUrl.getId());
        savedUrl.setCode(code);
        urlRepository.save(savedUrl);

        return code;
    }
}
