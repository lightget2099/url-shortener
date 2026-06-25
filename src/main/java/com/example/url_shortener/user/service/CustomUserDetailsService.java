package com.example.url_shortener.user.service;


import com.example.url_shortener.user.entity.UserEntity;
import com.example.url_shortener.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByUsername(username).
                orElseThrow(() -> new UsernameNotFoundException("User not found " + username));
        return new User(userEntity.getUsername(),
                userEntity.getPassword(),
                Collections.emptyList());
    }
}
