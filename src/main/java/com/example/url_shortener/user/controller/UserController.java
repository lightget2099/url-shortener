package com.example.url_shortener.user.controller;

import com.example.url_shortener.user.dto.UserRegistrationDto;
import com.example.url_shortener.user.dto.UserResponseDto;
import com.example.url_shortener.user.service.UserService;
import com.example.url_shortener.user.entity.UserEntity;
import com.example.url_shortener.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/register")
    public UserResponseDto register(@RequestBody UserRegistrationDto dto){
       UserEntity registeredUser = userService.registerUser(dto.getUsername(), dto.getPassword());
       return userMapper.toResponse(registeredUser);
    }
}
