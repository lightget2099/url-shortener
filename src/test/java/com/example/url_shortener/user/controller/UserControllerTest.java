package com.example.url_shortener.user.controller;

import com.example.url_shortener.config.JwtUtils;
import com.example.url_shortener.config.SecurityConfig;
import com.example.url_shortener.url.dto.UrlStatsResponseDto;
import com.example.url_shortener.url.service.UrlService;
import com.example.url_shortener.user.dto.UserLoginRequestDto;
import com.example.url_shortener.user.dto.UserLoginResponseDto;
import com.example.url_shortener.user.dto.UserRegistrationRequestDto;
import com.example.url_shortener.user.dto.UserRegistrationResponseDto;
import com.example.url_shortener.user.entity.UserEntity;
import com.example.url_shortener.user.mapper.UserMapper;
import com.example.url_shortener.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserMapper userMapper;

    @MockitoBean
    private UrlService urlService;

    @Test
    void register_ShouldReturnResponseDto_WhenRequestIsValid() throws Exception {
        String username = "bogdan_test";
        String password = "Password123";

        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto(username, password);
        UserEntity fakeUser = new UserEntity();
        fakeUser.setId(1L);
        fakeUser.setUsername(username);
        UserRegistrationResponseDto fakeResponse = new UserRegistrationResponseDto(1L, username);

        Mockito.when(userService.registerUser(username, password)).thenReturn(fakeUser);
        Mockito.when(userMapper.toResponse(fakeUser)).thenReturn(fakeResponse);

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value(username));
    }

    @Test
    void register_ShouldReturnBadRequest_WhenValidationFails() throws Exception {
        UserRegistrationRequestDto invalidDto = new UserRegistrationRequestDto("", "");

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.status").value("400 BAD_REQUEST"));
    }

    @Test
    void login_ShouldReturnToken_WhenRequestIsValid() throws Exception {
        String username = "bogdan_test";
        String password = "Password123";
        String token = "token123";

        UserLoginRequestDto fakeRequest = new UserLoginRequestDto(username, password);
        UserLoginResponseDto fakeResponse = new UserLoginResponseDto(token);

        Mockito.when(userService.login(fakeRequest)).thenReturn(fakeResponse);

        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fakeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token));
    }

    @Test
    void getUserUrls_ShouldReturnUrlStatsList_WhenUserIsAuthenticated() throws Exception {
        String username = "bogdan_test";

        UrlStatsResponseDto fakeUrlStats = new UrlStatsResponseDto("https://github.com", "abcdef", 5, null, null);
        List<UrlStatsResponseDto> fakeList = List.of(fakeUrlStats);

        Mockito.when(urlService.getUserUrls(username)).thenReturn(fakeList);

        mockMvc.perform(get("/api/v1/users/all/urls")
                        .with(user(username))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].url").value("https://github.com"))
                .andExpect(jsonPath("$[0].code").value("abcdef"))
                .andExpect(jsonPath("$[0].clicks").value(5));
    }
}