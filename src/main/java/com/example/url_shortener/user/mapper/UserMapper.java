package com.example.url_shortener.user.mapper;

import com.example.url_shortener.user.dto.UserRegistrationResponseDto;
import com.example.url_shortener.user.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserRegistrationResponseDto toResponse(UserEntity userEntity) {
        return new UserRegistrationResponseDto(userEntity.getId(), userEntity.getUsername());
    }
}
