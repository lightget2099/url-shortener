package com.example.url_shortener.user.service;

import com.example.url_shortener.user.repository.UserRepository;
import com.example.url_shortener.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserEntity registerUser(String username, String password) {
        if(username.length() < 3 || username.length() > 20) {
            throw new IllegalArgumentException("Username must be between 3 and 20 characters");
        }

        if(userRepository.existsByUsername(username)){
            throw new IllegalArgumentException("Username already exists");
        }

        if(!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")){
            throw new IllegalArgumentException("Password must contain at least + 1 uppercase letter, 1 digit, and 1 special character");
        }

        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));

        return userRepository.save(user);
    }
}
