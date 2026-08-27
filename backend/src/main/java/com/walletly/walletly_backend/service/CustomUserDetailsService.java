package com.walletly.walletly_backend.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.walletly.walletly_backend.exception.ErrorMessages;
import com.walletly.walletly_backend.exception.NotFoundException;
import com.walletly.walletly_backend.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {

        return userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException(ErrorMessages.USER_NOT_FOUND));
    }
}