package com.smartbudget.smartbudget_backend.dto.auth;

import com.smartbudget.smartbudget_backend.exception.ErrorMessages;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    @NotBlank(message = ErrorMessages.USER_EMAIL_REQUIRED)
    @Email(message = ErrorMessages.USER_EMAIL_NOT_VALID)
    private String email; 

    @NotBlank(message = ErrorMessages.USER_PASSWORD_REQUIRED)
    private String password; 

}