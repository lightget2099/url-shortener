package com.example.url_shortener.user.controller;

import com.example.url_shortener.url.dto.UrlStatsResponseDto;
import com.example.url_shortener.url.service.UrlService;
import com.example.url_shortener.user.dto.UserRegistrationDto;
import com.example.url_shortener.user.dto.UserResponseDto;
import com.example.url_shortener.user.service.UserService;
import com.example.url_shortener.user.entity.UserEntity;
import com.example.url_shortener.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final UrlService urlService;

    @PostMapping("/register")
    public UserResponseDto register(@RequestBody UserRegistrationDto dto){
       UserEntity registeredUser = userService.registerUser(dto.getUsername(), dto.getPassword());
       return userMapper.toResponse(registeredUser);
    }

    @GetMapping("/{userId}/urls")
    public List<UrlStatsResponseDto> getUserUrls(@PathVariable Long userId){
        return urlService.getUserUrls(userId);
    }
}
