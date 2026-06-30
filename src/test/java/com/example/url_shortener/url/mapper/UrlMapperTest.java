package com.example.url_shortener.url.mapper;

import com.example.url_shortener.url.dto.UrlStatsResponseDto;
import com.example.url_shortener.url.entity.UrlEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

class UrlMapperTest {
    private final UrlMapper urlMapper = new UrlMapper();

    @Test
    void toStatsDto_ShouldMapUrlEntityToUrlStatsResponseDto() {
        LocalDateTime now = LocalDateTime.now();

        UrlEntity entity = new UrlEntity();
        entity.setUrl("https://google.com");
        entity.setCode("abcdef");
        entity.setClickCount(10);
        entity.setCreatedAt(now);
        entity.setExpiresAt(now.plusDays(30));

        UrlStatsResponseDto dto = urlMapper.toStatsDto(entity);

        Assertions.assertNotNull(dto);
        Assertions.assertEquals(entity.getUrl(), dto.getUrl());
        Assertions.assertEquals(entity.getCode(), dto.getCode());
        Assertions.assertEquals(entity.getClickCount(), dto.getClicks());
        Assertions.assertEquals(entity.getCreatedAt(), dto.getCreatedAt());
        Assertions.assertEquals(entity.getExpiresAt(), dto.getExpiresAt());
    }

    @Test
    void toStatsDtoList_ShouldMapEntityListToDtoList() {
        UrlEntity entity1 = new UrlEntity();
        entity1.setUrl("https://google.com");
        entity1.setCode("gsajtt");

        UrlEntity entity2 = new UrlEntity();
        entity2.setUrl("https://github.com");
        entity2.setCode("gagsad");

        List<UrlEntity> entityList = List.of(entity1, entity2);

        List<UrlStatsResponseDto> dtoList = urlMapper.toStatsDtoList(entityList);

        Assertions.assertNotNull(dtoList);
        Assertions.assertEquals(2, dtoList.size());

        Assertions.assertEquals("https://google.com", dtoList.get(0).getUrl());
        Assertions.assertEquals("gsajtt", dtoList.get(0).getCode());

        Assertions.assertEquals("https://github.com", dtoList.get(1).getUrl());
        Assertions.assertEquals("gagsad", dtoList.get(1).getCode());
    }
}