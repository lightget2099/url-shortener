package com.example.url_shortener.user.repository;

import com.example.url_shortener.BaseIntegrationTest;
import com.example.url_shortener.user.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void saveUser_ShouldPersistInPostgreSQLContainer() {
        UserEntity user = new UserEntity();
        user.setUsername("docker_bogdan");
        user.setPassword("EncryptedPassword123");


        UserEntity savedUser = userRepository.save(user);

        assertNotNull(savedUser.getId());

        Optional<UserEntity> foundUser = userRepository.findByUsername("docker_bogdan");
        assertTrue(foundUser.isPresent());
        assertEquals("EncryptedPassword123", foundUser.get().getPassword());
    }
}