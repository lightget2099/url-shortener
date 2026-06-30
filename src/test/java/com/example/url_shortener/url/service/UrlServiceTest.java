package com.example.url_shortener.url.service;

import com.example.url_shortener.exception.UrlExpiredException;
import com.example.url_shortener.exception.UrlNotFoundException;
import com.example.url_shortener.exception.UserNotFoundException;
import com.example.url_shortener.url.dto.UrlStatsResponseDto;
import com.example.url_shortener.url.entity.UrlEntity;
import com.example.url_shortener.url.repository.UrlRepository;
import com.example.url_shortener.user.entity.UserEntity;
import com.example.url_shortener.user.repository.UserRepository;
import com.example.url_shortener.url.mapper.UrlMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UrlMapper urlMapper;

    @InjectMocks
    private UrlService urlService;

    private static final String URL_PREFIX_ERROR = "For shorten url: '";
    private static final String NOT_FOUND_SUFFIX = "' original URL was not found";
    private static final String USER_PREFIX_ERROR = "User with username '";
    private static final String NOT_EXIST_SUFFIX = "' doesn't exist";


    @Test
    void getOriginalUrl_ShouldIncrementClicksAndReturnUrl_WhenUrlIsActive() {
        String code = "abcdef";
        String originalUrl = "https://google.com";

        UrlEntity fakeUrl = new UrlEntity();
        fakeUrl.setCode(code);
        fakeUrl.setUrl(originalUrl);
        fakeUrl.setClickCount(0);
        fakeUrl.setExpiresAt(LocalDateTime.now().plusDays(1));

        Mockito.when(urlRepository.findByCode(code)).thenReturn(Optional.of(fakeUrl));

        String result = urlService.getOriginalUrl(code);

        Assertions.assertEquals(originalUrl, result);
        Assertions.assertEquals(1, fakeUrl.getClickCount());

        Mockito.verify(urlRepository, Mockito.times(1)).save(fakeUrl);
    }

    @Test
    void getOriginalUrl_ShouldThrowUrlExpiredException_WhenUrlIsNotActive() {
        String code = "abcdef";
        String originalUrl = "https://google.com";

        UrlEntity fakeUrl = new UrlEntity();
        fakeUrl.setCode(code);
        fakeUrl.setUrl(originalUrl);
        fakeUrl.setClickCount(0);
        fakeUrl.setExpiresAt(LocalDateTime.now().minusDays(1));

        Mockito.when(urlRepository.findByCode(code)).thenReturn(Optional.of(fakeUrl));

        UrlExpiredException exception = Assertions.assertThrows(
                UrlExpiredException.class,
                () -> urlService.getOriginalUrl(code)
        );

        String expectedMessage = URL_PREFIX_ERROR + code + " has expired";
        Assertions.assertEquals(expectedMessage, exception.getMessage());

        Mockito.verify(urlRepository, Mockito.times(1)).findByCode(code);
        Mockito.verify(urlRepository, Mockito.never()).save(fakeUrl);
    }

    @Test
    void getOriginalUrl_ShouldThrowUrlNotFoundException_WhenUrlIDoesntExist() {
        String code = "abcdef";

        Mockito.when(urlRepository.findByCode(code)).thenReturn(Optional.empty());

        UrlNotFoundException exception = Assertions.assertThrows(
                UrlNotFoundException.class,
                () -> urlService.getOriginalUrl(code)
        );

        String expectedMessage = URL_PREFIX_ERROR + code + NOT_FOUND_SUFFIX;
        Assertions.assertEquals(expectedMessage, exception.getMessage());

        Mockito.verify(urlRepository, Mockito.times(1)).findByCode(code);
        Mockito.verify(urlRepository, Mockito.never()).save(Mockito.any(UrlEntity.class));
    }

    @Test
    void shortenUrl_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        String username = "non_existent_user";
        String url = "https://google.com";

        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        UserNotFoundException exception = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> urlService.shortenUrl(url, username)
        );

        String expectedMessage = USER_PREFIX_ERROR + username + NOT_EXIST_SUFFIX;
        Assertions.assertEquals(expectedMessage, exception.getMessage());

        Mockito.verify(urlRepository, Mockito.never()).findByUserIdAndUrl(Mockito.anyLong(), Mockito.anyString());
    }

    @Test
    void shortenUrl_ShouldReturnExistingCode_WhenUrlAlreadyExists() {
        String username = "existing_user";
        String originalUrl = "https://google.com";

        UserEntity fakeUser = new UserEntity();
        fakeUser.setUsername(username);
        fakeUser.setId(1L);

        UrlEntity fakeUrl = new UrlEntity();
        fakeUrl.setCode("oldCode");
        fakeUrl.setUser(fakeUser);

        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(fakeUser));
        Mockito.when(urlRepository.findByUserIdAndUrl(fakeUser.getId(), originalUrl)).thenReturn(Optional.of(fakeUrl));

        String result = urlService.shortenUrl(originalUrl, username);

        Assertions.assertEquals(fakeUrl.getCode(), result);

        Mockito.verify(userRepository, Mockito.times(1)).findByUsername(username);
        Mockito.verify(urlRepository, Mockito.times(1)).findByUserIdAndUrl(fakeUser.getId(), originalUrl);
        Mockito.verify(urlRepository, Mockito.never()).save(Mockito.any(UrlEntity.class));
    }

    @Test
    void shortenUrl_ShouldCreateandReturnNewCode_WhenUrlIsNew() {
        String originalUrl = "https://google.com";
        String username = "existing_user";

        UserEntity fakeUser = new UserEntity();
        fakeUser.setUsername(username);
        fakeUser.setId(1L);

        UrlEntity urlWithId = new UrlEntity();
        urlWithId.setId(100L);
        urlWithId.setUrl(originalUrl);
        urlWithId.setUser(fakeUser);
        urlWithId.setCode("");

        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(fakeUser));
        Mockito.when(urlRepository.findByUserIdAndUrl(fakeUser.getId(), originalUrl)).thenReturn(Optional.empty());
        Mockito.when(urlRepository.save(Mockito.any(UrlEntity.class))).thenReturn(urlWithId);

        String result = urlService.shortenUrl(originalUrl, username);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(6, result.length());

        Mockito.verify(userRepository, Mockito.times(1)).findByUsername(username);
        Mockito.verify(urlRepository, Mockito.times(1)).findByUserIdAndUrl(fakeUser.getId(), originalUrl);
        Mockito.verify(urlRepository, Mockito.times(2)).save(Mockito.any(UrlEntity.class));
    }

    @Test
    void deleteUrl_ShouldThrowUrlNotFoundException_WhenUrlDoesntExist() {
        String code = "abcdef";
        String username = "existing_user";

        Mockito.when(urlRepository.findByCode(code)).thenReturn(Optional.empty());

        UrlNotFoundException exception = Assertions.assertThrows(
                UrlNotFoundException.class,
                () -> urlService.deleteUrl(code, username)
        );

        String expectedMessage = URL_PREFIX_ERROR + code + NOT_FOUND_SUFFIX;
        Assertions.assertEquals(expectedMessage, exception.getMessage());

        Mockito.verify(urlRepository, Mockito.times(1)).findByCode(code);
        Mockito.verify(urlRepository, Mockito.never()).delete(Mockito.any(UrlEntity.class));
    }

    @Test
    void deleteUrl_ShouldThrowAccessDeniedException_WhenUserIsNotOwner() {
        String code = "abcdef";
        String attackerUsername = "attacker_user";

        UserEntity owner = new UserEntity();
        owner.setUsername("owner_user");

        UrlEntity fakeUrl = new UrlEntity();
        fakeUrl.setCode(code);
        fakeUrl.setUser(owner);


        Mockito.when(urlRepository.findByCode(code)).thenReturn(Optional.of(fakeUrl));

        AccessDeniedException exception = Assertions.assertThrows(
                AccessDeniedException.class,
                () -> urlService.deleteUrl(code, attackerUsername)
        );

        Assertions.assertEquals("Ви не маєте права на видалення цього посилання", exception.getMessage());

        Mockito.verify(urlRepository, Mockito.times(1)).findByCode(code);
        Mockito.verify(urlRepository, Mockito.never()).delete(Mockito.any(UrlEntity.class));
    }

    @Test
    void deleteUrl_ShouldDeleteUrl_WhenUserIsOwner() {
        String code = "abcdef";
        String username = "true_owner";

        UserEntity owner = new UserEntity();
        owner.setUsername(username);

        UrlEntity fakeUrl = new UrlEntity();
        fakeUrl.setCode(code);
        fakeUrl.setUser(owner);

        Mockito.when(urlRepository.findByCode(code)).thenReturn(Optional.of(fakeUrl));

        urlService.deleteUrl(code, username);


        Mockito.verify(urlRepository, Mockito.times(1)).findByCode(code);
        Mockito.verify(urlRepository, Mockito.times(1)).delete(fakeUrl);
    }

    @Test
    void getUrlStats_ShouldReturnStatsDto_WhenUrlExists() {
        String code = "abcdef";
        UrlEntity fakeUrl = new UrlEntity();
        fakeUrl.setCode(code);

        UrlStatsResponseDto fakeDto = new UrlStatsResponseDto("https://google.com", code, 0, null, null);

        Mockito.when(urlRepository.findByCode(code)).thenReturn(Optional.of(fakeUrl));
        Mockito.when(urlMapper.toStatsDto(fakeUrl)).thenReturn(fakeDto);

        UrlStatsResponseDto result = urlService.getUrlStats(code);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(fakeDto, result);

        Mockito.verify(urlRepository, Mockito.times(1)).findByCode(code);
        Mockito.verify(urlMapper, Mockito.times(1)).toStatsDto(fakeUrl);
    }

    @Test
    void getUserUrls_ShouldReturnDtoList_WhenUserExists() {
        String username = "existing_user";

        UserEntity fakeUser = new UserEntity();
        fakeUser.setUsername(username);
        fakeUser.setId(1L);

        UrlEntity fakeUrl = new UrlEntity();
        fakeUrl.setUser(fakeUser);

        List<UrlEntity> fakeUrls = List.of(fakeUrl);

        UrlStatsResponseDto fakeDto = new UrlStatsResponseDto("https://google.com", "abcdef", 0, null, null);
        List<UrlStatsResponseDto> fakeDtoList = List.of(fakeDto);

        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(fakeUser));
        Mockito.when(urlRepository.findByUserId(fakeUser.getId())).thenReturn(fakeUrls);
        Mockito.when(urlMapper.toStatsDtoList(fakeUrls)).thenReturn(fakeDtoList);

        List<UrlStatsResponseDto> result = urlService.getUserUrls(username);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(fakeDtoList, result);

        Mockito.verify(userRepository, Mockito.times(1)).findByUsername(username);
        Mockito.verify(urlRepository, Mockito.times(1)).findByUserId(fakeUser.getId());
        Mockito.verify(urlMapper, Mockito.times(1)).toStatsDtoList(fakeUrls);
    }

}