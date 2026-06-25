package com.example.url_shortener.url.repository;

import com.example.url_shortener.url.entity.UrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<UrlEntity, Long> {
    Optional<UrlEntity> findByCode(String code);
    List<UrlEntity> findByUserId(Long userId);

    @Modifying
    @Transactional
    void deleteByExpiresAtBefore(LocalDateTime now);
}
