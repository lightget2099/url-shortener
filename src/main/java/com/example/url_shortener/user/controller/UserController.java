package com.example.url_shortener.user.controller;

import com.example.url_shortener.user.dto.UserLoginRequestDto;
import com.example.url_shortener.user.dto.UserLoginResponseDto;
import com.example.url_shortener.user.dto.UserRegistrationRequestDto;
import com.example.url_shortener.user.dto.UserRegistrationResponseDto;
import com.example.url_shortener.user.service.UserService;
import com.example.url_shortener.user.entity.UserEntity;
import com.example.url_shortener.user.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Користувачі та Безпека",
description = "Ендпоінти для реєстрації, логіну")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/register")
    @Operation(summary = "Реєстрація",
            description = "Процесс реєстрації користувача")
    public UserRegistrationResponseDto register(@RequestBody @Valid UserRegistrationRequestDto dto){
       UserEntity registeredUser = userService.registerUser(dto.getUsername(), dto.getPassword());
       return userMapper.toResponse(registeredUser);
    }

    @PostMapping("/login")
    @Operation(summary = "Логін",
            description = "Процес залогінення в сервіс")
    public UserLoginResponseDto login(@Valid @RequestBody UserLoginRequestDto dto) {
        return userService.login(dto);
    }
}
