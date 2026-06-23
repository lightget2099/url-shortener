package com.example.url_shortener.user;

import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponseDto toResponse(UserEntity userEntity) {
        return new UserResponseDto(userEntity.getId(), userEntity.getUsername());
    }
}
