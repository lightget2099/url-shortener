package com.example.url_shortener.user.mapper;

import com.example.url_shortener.user.dto.UserRegistrationResponseDto;
import com.example.url_shortener.user.entity.UserEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class UserMapperTest {
    private final UserMapper userMapper = new UserMapper();

    @Test
    void toResponse_ShouldMapUserEntityToUserRegistrationResponseDto() {
        UserEntity user = new UserEntity();
        user.setUsername("username");
        user.setId(1L);

        UserRegistrationResponseDto response = userMapper.toResponse(user);

        Assertions.assertEquals(user.getId(), response.getId());
        Assertions.assertEquals(user.getUsername(), response.getUsername());
    }
}
