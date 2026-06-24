package com.example.url_shortener.url.mapper;

import com.example.url_shortener.url.dto.UrlStatsResponseDto;
import com.example.url_shortener.url.entity.UrlEntity;
import org.springframework.stereotype.Component;

@Component
public class UrlMapper {

    public UrlStatsResponseDto toStatsDto(UrlEntity urlEntity) {
        return new UrlStatsResponseDto(urlEntity.getUrl(),
                urlEntity.getCode(),
                urlEntity.getClickCount(),
                urlEntity.getCreatedAt(),
                urlEntity.getExpiresAt());
    }
}
