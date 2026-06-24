package com.example.url_shortener.url.mapper;

import com.example.url_shortener.url.dto.UrlStatsResponseDto;
import com.example.url_shortener.url.entity.UrlEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UrlMapper {

    public UrlStatsResponseDto toStatsDto(UrlEntity urlEntity) {
        return new UrlStatsResponseDto(urlEntity.getUrl(),
                urlEntity.getCode(),
                urlEntity.getClickCount(),
                urlEntity.getCreatedAt(),
                urlEntity.getExpiresAt());
    }

    public List<UrlStatsResponseDto> toStatsDtoList(List<UrlEntity> urlEntityList) {
        return urlEntityList.stream()
                .map(this::toStatsDto)
                .toList();
    }
}
