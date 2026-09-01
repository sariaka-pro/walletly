package com.walletly.walletly_backend.service;

import java.util.Locale;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.walletly.walletly_backend.dto.auth.LoginRequest;
import com.walletly.walletly_backend.dto.auth.LoginResponse;
import com.walletly.walletly_backend.dto.auth.RegisterRequest;
import com.walletly.walletly_backend.exception.BadRequestException;
import com.walletly.walletly_backend.exception.ErrorMessages;
import com.walletly.walletly_backend.exception.NotFoundException;
import com.walletly.walletly_backend.exception.UnauthorizedException;
import com.walletly.walletly_backend.model.User;
import com.walletly.walletly_backend.model.enums.Role;
import com.walletly.walletly_backend.repository.UserRepository;

@Service
public class AuthService {

    /// 1 - on injecter dans le constructor le userrepository et passwordencoder 
    private final UserRepository userRepository; 
    private final PasswordEncoder passwordEncoder; 
    private final JwtService jwtService;
    private final InputSanitizer inputSanitizer;
    private final String dummyPasswordHash;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, InputSanitizer inputSanitizer) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.inputSanitizer = inputSanitizer;
        this.dummyPasswordHash = passwordEncoder.encode("walletly-dummy-password");
    }

    public void register(RegisterRequest request) {

        String normalizedEmail = request.getEmail().trim().toLowerCase(Locale.ROOT);

        if(userRepository.existsByEmail(normalizedEmail)) {
            throw new BadRequestException(ErrorMessages.USER_EMAIL_ALREADY_EXISTS);
        }        

        String hashedPassword = passwordEncoder.encode(request.getPassword()); 

        String firstName = request.getFirstName();
        if (firstName != null) {
            firstName = inputSanitizer.sanitizePlainText(firstName, "user.firstName");
            if (firstName.isEmpty()) {
                firstName = null;
            }
        }

        String lastName = request.getLastName();
        if (lastName != null) {
            lastName = inputSanitizer.sanitizePlainText(lastName, "user.lastName");
            if (lastName.isEmpty()) {
                lastName = null;
            }
        }

        User user = User.builder()
            .email(normalizedEmail)
            .firstName(firstName)
            .lastName(lastName)
            .password(hashedPassword)
            .role(Role.USER)
            .build();

        userRepository.save(user); 
    }; 

    public LoginResponse login(LoginRequest logRequest) {

        String normalizedEmail = logRequest.getEmail().trim().toLowerCase(Locale.ROOT);
        Optional<User> existingUser = userRepository.findByEmail(normalizedEmail);

        if (existingUser.isEmpty()) {
            // Effectuer quand même une vérification BCrypt afin de limiter les différences
            // de temps de réponse entre un compte inconnu et un mauvais mot de passe.
            passwordEncoder.matches(logRequest.getPassword(), dummyPasswordHash);
            throw new UnauthorizedException(ErrorMessages.INVALID_CREDENTIALS);
        }

        User user = existingUser.get();
        if (!passwordEncoder.matches(logRequest.getPassword(), user.getPassword())) {
            throw new UnauthorizedException(ErrorMessages.INVALID_CREDENTIALS);
        }

        String token = jwtService.generateToken(user);

        return LoginResponse.builder()
            .token(token)
            .id(user.getId())
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .build();
    }

    /**
     * Récupérer un utilisateur par son ID
     * Utilisé par BudgetService et autres services
     */
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException(ErrorMessages.USER_NOT_FOUND));
    }
}
