package com.example.url_shortener.url.repository;

import com.example.url_shortener.url.entity.UrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UrlRepository extends JpaRepository<UrlEntity, Long> {
    Optional<UrlEntity> findByCode(String code);
}
