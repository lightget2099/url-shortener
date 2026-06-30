package com.example.url_shortener.user.service;

import com.example.url_shortener.config.JwtUtils;
import com.example.url_shortener.user.dto.UserLoginRequestDto;
import com.example.url_shortener.user.dto.UserLoginResponseDto;
import com.example.url_shortener.user.entity.UserEntity;
import com.example.url_shortener.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private UserService userService;

    @Test
    void registerUser_ShouldRegisterSuccessfully_WhenUsernameIsUnique() {
        String username = "bogdan_dev";
        String rawPassword = "Password123";
        String encodedPassword = "encoded_password_in_db";

        Mockito.when(userRepository.existsByUsername(username)).thenReturn(false);
        Mockito.when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        UserEntity savedUser = new UserEntity();
        savedUser.setId(1L);
        savedUser.setUsername(username);
        savedUser.setPassword(encodedPassword);

        Mockito.when(userRepository.save(Mockito.any(UserEntity.class))).thenReturn(savedUser);


        UserEntity result = userService.registerUser(username, rawPassword);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1L, result.getId());
        Assertions.assertEquals(username, result.getUsername());
        Assertions.assertEquals(encodedPassword, result.getPassword());

        Mockito.verify(userRepository, Mockito.times(1)).existsByUsername(username);
        Mockito.verify(passwordEncoder, Mockito.times(1)).encode(rawPassword);
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(UserEntity.class));
    }

    @Test
    void registerUser_ShouldThrowException_WhenUsernameAlreadyExists() {
        String username = "existing_user";
        String password = "Password123";


        Mockito.when(userRepository.existsByUsername(username)).thenReturn(true);

        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> userService.registerUser(username, password)
        );

        Assertions.assertEquals("Username already exists", exception.getMessage());

        Mockito.verify(passwordEncoder, Mockito.never()).encode(Mockito.anyString());
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(UserEntity.class));
    }

    @Test
    void login_ShouldReturnToken_WhenCredentialsAreValid() {
        String username = "existing_user";
        String rawPassword = "Password123";
        String encodedPassword = "encoded_password_in_db";
        String fakeToken = "my-fake-jwt-token";

        UserEntity fakeUser = new UserEntity();
        fakeUser.setUsername(username);
        fakeUser.setPassword(encodedPassword);
        UserLoginRequestDto loginDto = new UserLoginRequestDto(username, rawPassword);

        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(fakeUser));
        Mockito.when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);
        Mockito.when(jwtUtils.generateToken(username)).thenReturn(fakeToken);

        UserLoginResponseDto response = userService.login(loginDto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(fakeToken, response.getToken());

        Mockito.verify(userRepository, Mockito.times(1)).findByUsername(username);
        Mockito.verify(passwordEncoder, Mockito.times(1)).matches(rawPassword, encodedPassword);
        Mockito.verify(jwtUtils, Mockito.times(1)).generateToken(username);
    }

    @Test
    void login_ShouldThrowException_WhenCredentialsAreInvalid() {
        String username = "unknown_user";
        String rawPassword = "Password123";
        UserLoginRequestDto loginDto = new UserLoginRequestDto(username, rawPassword);

        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> userService.login(loginDto)
        );

        Assertions.assertEquals("Invalid username or password", exception.getMessage());

        Mockito.verify(userRepository, Mockito.times(1)).findByUsername(username);
        Mockito.verify(passwordEncoder, Mockito.never()).matches(Mockito.anyString(), Mockito.anyString());
        Mockito.verify(jwtUtils, Mockito.never()).generateToken(Mockito.anyString());
    }

    @Test
    void login_ShouldThrowException_WhenPasswordIsIncorrect() {
        String username = "existing_user";
        String rawPassword = "Password123";
        String encodedPassword = "encoded_password_in_db";

        UserEntity fakeUser = new UserEntity();
        fakeUser.setUsername(username);
        fakeUser.setPassword(encodedPassword);
        UserLoginRequestDto loginDto = new UserLoginRequestDto(username, rawPassword);


        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(fakeUser));
        Mockito.when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);

        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> userService.login(loginDto)
        );

        Assertions.assertEquals("Invalid username or password", exception.getMessage());

        Mockito.verify(userRepository, Mockito.times(1)).findByUsername(username);
        Mockito.verify(passwordEncoder, Mockito.times(1)).matches(rawPassword, encodedPassword);
        Mockito.verify(jwtUtils, Mockito.never()).generateToken(Mockito.anyString());
    }
}