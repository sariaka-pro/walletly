package com.walletly.walletly_backend.dto.auth;

import com.walletly.walletly_backend.exception.ErrorMessages;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
    
    @NotBlank(message = ErrorMessages.USER_EMAIL_REQUIRED)
    @Email(message = ErrorMessages.USER_EMAIL_NOT_VALID)
    private String email; 

    @NotBlank(message = ErrorMessages.USER_PASSWORD_REQUIRED)
    private String password;

    private String firstName;

    private String lastName;
}