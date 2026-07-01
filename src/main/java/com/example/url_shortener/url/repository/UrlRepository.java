package com.example.url_shortener.url.repository;

import com.example.url_shortener.url.entity.UrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<UrlEntity, Long> {
    Optional<UrlEntity> findByCode(String code);
    List<UrlEntity> findByUserId(Long userId);
    Optional<UrlEntity> findByUserIdAndUrl(Long userId, String url);

    @Modifying
    @Transactional
    void deleteByExpiresAtBefore(LocalDateTime now);

    @Modifying
    @Query("""
    update UrlEntity u
    set u.clickCount = u.clickCount + 1
    where u.code = :code
    """)
    int incrementClickCount(@Param("code") String code);
}
