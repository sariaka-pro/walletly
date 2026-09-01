package com.walletly.walletly_backend.dto.auth;

import java.util.Locale;

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
public class LoginRequest {

    @NotBlank(message = ErrorMessages.USER_EMAIL_REQUIRED)
    @Email(message = ErrorMessages.USER_EMAIL_NOT_VALID)
    private String email; 

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim().toLowerCase(Locale.ROOT); // converti en minuscule
    }

    @NotBlank(message = ErrorMessages.USER_PASSWORD_REQUIRED)
    private String password; 

}
