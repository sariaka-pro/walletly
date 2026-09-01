package com.walletly.walletly_backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.walletly.walletly_backend.dto.auth.LoginRequest;
import com.walletly.walletly_backend.dto.auth.LoginResponse;
import com.walletly.walletly_backend.exception.ErrorMessages;
import com.walletly.walletly_backend.exception.UnauthorizedException;
import com.walletly.walletly_backend.model.User;
import com.walletly.walletly_backend.model.enums.Role;
import com.walletly.walletly_backend.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private InputSanitizer inputSanitizer;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        when(passwordEncoder.encode("walletly-dummy-password")).thenReturn("dummy-password-hash");
        authService = new AuthService(userRepository, passwordEncoder, jwtService, inputSanitizer);
    }

    @Test
    void loginReturnsSameUnauthorizedErrorWhenEmailDoesNotExist() {
        LoginRequest request = LoginRequest.builder()
            .email(" UNKNOWN@EXAMPLE.COM ")
            .password("secret")
            .build();
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        UnauthorizedException exception = assertThrows(
            UnauthorizedException.class,
            () -> authService.login(request)
        );

        assertEquals(ErrorMessages.INVALID_CREDENTIALS, exception.getMessage());
        verify(passwordEncoder).matches("secret", "dummy-password-hash");
    }

    @Test
    void loginReturnsSameUnauthorizedErrorWhenPasswordIsWrong() {
        User user = User.builder()
            .id(1L).email("user@example.com").password("real-password-hash").role(Role.USER).build();
        LoginRequest request = LoginRequest.builder()
            .email("USER@EXAMPLE.COM")
            .password("wrong-password")
            .build();
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "real-password-hash")).thenReturn(false);

        UnauthorizedException exception = assertThrows(
            UnauthorizedException.class,
            () -> authService.login(request)
        );

        assertEquals(ErrorMessages.INVALID_CREDENTIALS, exception.getMessage());
    }

    @Test
    void loginNormalizesEmailAndReturnsTokenWhenCredentialsAreValid() {
        User user = User.builder()
            .id(1L).email("user@example.com").password("real-password-hash").role(Role.USER).build();
        LoginRequest request = LoginRequest.builder()
            .email(" USER@EXAMPLE.COM ")
            .password("correct-password")
            .build();
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("correct-password", "real-password-hash")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        LoginResponse response = authService.login(request);

        assertEquals("jwt-token", response.getToken());
        assertEquals("user@example.com", response.getEmail());
        verify(userRepository).findByEmail("user@example.com");
    }
}
