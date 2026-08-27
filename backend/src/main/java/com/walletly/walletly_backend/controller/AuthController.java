package com.walletly.walletly_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.walletly.walletly_backend.dto.auth.LoginRequest;
import com.walletly.walletly_backend.dto.auth.LoginResponse;
import com.walletly.walletly_backend.dto.auth.RegisterRequest;
import com.walletly.walletly_backend.service.AuthService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/auth")
public class AuthController {


    // 1- injecte AuthService 
    private final AuthService authService; 

    public AuthController(AuthService authService) {
        this.authService = authService; 
    }

    // 2. Créer le endpoint vers "/resister" 
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request){
        authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("User created successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        
        LoginResponse response = authService.login(request);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(response);
    }
    

}