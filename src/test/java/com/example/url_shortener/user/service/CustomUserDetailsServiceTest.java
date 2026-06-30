package com.example.url_shortener.user.service;

import com.example.url_shortener.user.entity.UserEntity;
import com.example.url_shortener.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private UserEntity fakeUserEntity;

    @BeforeEach
    void setUp() {
        fakeUserEntity = new UserEntity();
        fakeUserEntity.setId(1L);
        fakeUserEntity.setUsername("bogdan_test");
        fakeUserEntity.setPassword("encrypted_password123");
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        String username = "bogdan_test";

        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(fakeUserEntity));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        assertNotNull(userDetails);
        assertEquals(fakeUserEntity.getUsername(), userDetails.getUsername());
        assertEquals(fakeUserEntity.getPassword(), userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().isEmpty());
    }

    @Test
    void loadUserByUsername_ShouldThrowUsernameNotFoundException_WhenUserDoesNotExist() {
        String unknownUser = "anonymous";

        Mockito.when(userRepository.findByUsername(unknownUser)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername(unknownUser));

        assertEquals("User not found " + unknownUser, exception.getMessage());
    }
}