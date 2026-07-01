package com.example.url_shortener.url.controller;


import com.example.url_shortener.config.JwtUtils;
import com.example.url_shortener.config.SecurityConfig;
import com.example.url_shortener.exception.UrlExpiredException;
import com.example.url_shortener.exception.UrlNotFoundException;
import com.example.url_shortener.url.dto.UrlRequestDto;
import com.example.url_shortener.url.dto.UrlStatsResponseDto;
import com.example.url_shortener.url.dto.UrlUpdateDto;
import com.example.url_shortener.url.service.UrlService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({UrlController.class, RedirectController.class})
@Import(SecurityConfig.class)
class UrlControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private UrlService urlService;

    private static final String URL_PREFIX_ERROR = "For shorten url: '";
    private static final String NOT_FOUND_SUFFIX = "' original URL was not found";

    @Test
    void createShortenUrl_ShouldReturnCode_WhenRequestIsValid() throws Exception {
        String username = "bogdan_test";
        String longUrl = "https://www.github.com";
        String generatedCode = "gFFfVO";
        String expectedShortUrl = "http://localhost:8080/r/" + generatedCode;

        UrlRequestDto requestDto = new UrlRequestDto(longUrl, null);
        Mockito.when(urlService.shortenUrl(any(UrlRequestDto.class), eq(username))).thenReturn(generatedCode);

        mockMvc.perform(post("/api/v1/urls")
                        .with(user(username))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(generatedCode))
                .andExpect(jsonPath("$.shortUrl").value(expectedShortUrl));
    }

    @Test
    void createShortenUrl_ShouldReturnBadRequest_WhenIllegalArgumentExceptionIsThrown() throws Exception {
        String username = "bogdan_test";
        String badUrl = "https://github.com";
        UrlRequestDto requestDto = new UrlRequestDto(badUrl, null);

        Mockito.when(urlService.shortenUrl(any(UrlRequestDto.class), eq(username)))
                .thenThrow(new IllegalArgumentException("Invalid argument provided"));

        mockMvc.perform(post("/api/v1/urls")
                        .with(user(username))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400 BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Invalid argument provided"));
    }

    @Test
    void getOriginalUrl_ShouldRedirectToLongUrl() throws Exception {
        String code = "gFFfVO";
        String originalUrl = "https://github.com";

        Mockito.when(urlService.getOriginalUrl(code)).thenReturn(originalUrl);


        mockMvc.perform(get("/r/{code}", code)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound())
                .andExpect(header().string(HttpHeaders.LOCATION, originalUrl));
    }

    @Test
    void getOriginalUrl_ShouldReturnNotFound_WhenCodeDoesNotExist() throws Exception {
        String unknownCode = "missingCode";

        Mockito.when(urlService.getOriginalUrl(unknownCode))
                .thenThrow(new UrlNotFoundException(URL_PREFIX_ERROR + unknownCode + NOT_FOUND_SUFFIX));

        mockMvc.perform(get("/r/{code}", unknownCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("404 NOT_FOUND"))
                .andExpect(jsonPath("$.message").value(URL_PREFIX_ERROR + unknownCode + NOT_FOUND_SUFFIX));
    }

    @Test
    void getOriginalUrl_ShouldReturnStatusGone_WhenUrlHasExpired() throws Exception {
        String expiredCode = "gFFfVO";

        Mockito.when(urlService.getOriginalUrl(expiredCode))
                .thenThrow(new UrlExpiredException(URL_PREFIX_ERROR + expiredCode + " has expired"));

        mockMvc.perform(get("/r/{code}", expiredCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isGone())
                .andExpect(jsonPath("$.status").value("410 GONE"))
                .andExpect(jsonPath("$.message").value(URL_PREFIX_ERROR + expiredCode + " has expired"));
    }

    @Test
    void getUrlStats_ShouldReturnFullStats() throws Exception {
        String code = "gFFfVO";
        String originalUrl = "https://github.com";
        int clicks = 5;
        LocalDateTime now = LocalDateTime.now();

        UrlStatsResponseDto urlStatsResponseDto = new UrlStatsResponseDto(originalUrl, code, clicks, now, now.plusMonths(1));
        Mockito.when(urlService.getUrlStats(code)).thenReturn(urlStatsResponseDto);

        mockMvc.perform(get("/api/v1/urls/" + code + "/stats")
                        .with(user("bogdan_test"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value(originalUrl))
                .andExpect(jsonPath("$.code").value(code))
                .andExpect(jsonPath("$.clicks").value(clicks));
    }

    @Test
    void deleteUrl_ShouldReturnStatusOk() throws Exception {
        String username = "bogdan_test";
        String code = "gFFfVO";

        Mockito.doNothing().when(urlService).deleteUrl(code, username);

        mockMvc.perform(delete("/api/v1/urls/" + code)
                .with(user(username))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.verify(urlService, Mockito.times(1)).deleteUrl(code, username);
    }

    @Test
    void getUserUrls_ShouldReturnUrlStatsList_WhenUserIsAuthenticated() throws Exception {
        String username = "bogdan_test";

        UrlStatsResponseDto fakeUrlStats = new UrlStatsResponseDto("https://github.com", "abcdef", 5, null, null);
        List<UrlStatsResponseDto> fakeList = List.of(fakeUrlStats);

        Mockito.when(urlService.getUserUrls(username)).thenReturn(fakeList);

        mockMvc.perform(get("/api/v1/urls")
                        .with(user(username))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].url").value("https://github.com"))
                .andExpect(jsonPath("$[0].code").value("abcdef"))
                .andExpect(jsonPath("$[0].clicks").value(5));
    }

    @Test
    void getActiveUrls_ShouldReturnUrlStatsList_WhenUserIsAuthenticated() throws Exception {
        String username = "bogdan_test";

        UrlStatsResponseDto fakeUrlStats = new UrlStatsResponseDto("https://github.com", "abcdef", 5, null, null);
        List<UrlStatsResponseDto> fakeList = List.of(fakeUrlStats);

        Mockito.when(urlService.getActiveUserUrls(username)).thenReturn(fakeList);

        mockMvc.perform(get("/api/v1/urls/active")
                .with(user(username))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].url").value("https://github.com"))
                .andExpect(jsonPath("$[0].code").value("abcdef"))
                .andExpect(jsonPath("$[0].clicks").value(5));
    }

    @Test
    void updateUrl_ShouldReturnStatusOk() throws Exception {
        String username = "bogdan_test";
        String code = "abcdef";
        String newUrl = "https://github.com";

        UrlUpdateDto updateDto = new UrlUpdateDto(newUrl);
        UrlStatsResponseDto fakeUrlStats = new UrlStatsResponseDto("https://github.com", "abcdef", 5, null, null);

        Mockito.when(urlService.updateOriginalUrl(code, newUrl, username)).thenReturn(fakeUrlStats);

        mockMvc.perform(patch("/api/v1/urls/{code}", code)
                        .with(user(username))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value(newUrl))
                .andExpect(jsonPath("$.code").value(code));

    }
}
