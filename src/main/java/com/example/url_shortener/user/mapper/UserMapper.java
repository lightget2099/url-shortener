package com.example.url_shortener.user.mapper;

import com.example.url_shortener.user.dto.UserResponseDto;
import com.example.url_shortener.user.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponseDto toResponse(UserEntity userEntity) {
        return new UserResponseDto(userEntity.getId(), userEntity.getUsername());
    }
}
