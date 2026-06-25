package com.example.url_shortener.user.controller;

import com.example.url_shortener.url.dto.UrlStatsResponseDto;
import com.example.url_shortener.url.service.UrlService;
import com.example.url_shortener.user.dto.UserLoginRequestDto;
import com.example.url_shortener.user.dto.UserLoginResponseDto;
import com.example.url_shortener.user.dto.UserRegistrationRequestDto;
import com.example.url_shortener.user.dto.UserRegistrationResponseDto;
import com.example.url_shortener.user.service.UserService;
import com.example.url_shortener.user.entity.UserEntity;
import com.example.url_shortener.user.mapper.UserMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    public UserRegistrationResponseDto register(@RequestBody UserRegistrationRequestDto dto){
       UserEntity registeredUser = userService.registerUser(dto.getUsername(), dto.getPassword());
       return userMapper.toResponse(registeredUser);
    }

    @GetMapping("/all/urls")
    public List<UrlStatsResponseDto> getUserUrls(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        return urlService.getUserUrls(username);
    }

    @PostMapping("/login")
    public UserLoginResponseDto login(@Valid @RequestBody UserLoginRequestDto dto) {
        return userService.login(dto);
    }
}
