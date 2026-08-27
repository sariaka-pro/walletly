package com.walletly.walletly_backend.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.walletly.walletly_backend.dto.auth.LoginRequest;
import com.walletly.walletly_backend.dto.auth.LoginResponse;
import com.walletly.walletly_backend.dto.auth.RegisterRequest;
import com.walletly.walletly_backend.exception.BadRequestException;
import com.walletly.walletly_backend.exception.ErrorMessages;
import com.walletly.walletly_backend.exception.NotFoundException;
import com.walletly.walletly_backend.model.User;
import com.walletly.walletly_backend.model.enums.Role;
import com.walletly.walletly_backend.repository.UserRepository;

@Service
public class AuthService {

    /// 1 - on injecter dans le constructor le userrepository et passwordencoder 
    private final UserRepository userRepository; 
    private final PasswordEncoder passwordEncoder; 
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public void register(RegisterRequest request) {

        if(userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException(ErrorMessages.USER_EMAIL_ALREADY_EXISTS);
        }        

        String hashedPassword = passwordEncoder.encode(request.getPassword()); 

        String firstName = request.getFirstName();
        if (firstName != null) {
            firstName = firstName.trim();
            if (firstName.isEmpty()) {
                firstName = null;
            }
        }

        String lastName = request.getLastName();
        if (lastName != null) {
            lastName = lastName.trim();
            if (lastName.isEmpty()) {
                lastName = null;
            }
        }

        User user = User.builder()
            .email(request.getEmail())
            .firstName(firstName)
            .lastName(lastName)
            .password(hashedPassword)
            .role(Role.USER)
            .build();

        userRepository.save(user); 
    }; 

    public LoginResponse login(LoginRequest logRequest) {

        User user = userRepository.findByEmail(logRequest.getEmail())
                .orElseThrow(() -> new NotFoundException(ErrorMessages.USER_NOT_FOUND));

        if(user == null || !passwordEncoder.matches(logRequest.getPassword(), user.getPassword())) {
            throw new BadRequestException(ErrorMessages.INVALID_CREDENTIALS);
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
