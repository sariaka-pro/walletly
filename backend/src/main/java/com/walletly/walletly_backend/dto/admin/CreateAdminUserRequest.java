package com.walletly.walletly_backend.dto.admin;

import com.walletly.walletly_backend.exception.ErrorMessages;
import com.walletly.walletly_backend.model.enums.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateAdminUserRequest {

    @NotBlank(message = ErrorMessages.USER_FIRST_NAME_REQUIRED)
    private String firstName;

    @NotBlank(message = ErrorMessages.USER_LAST_NAME_REQUIRED)
    private String lastName;

    @NotBlank(message = ErrorMessages.USER_EMAIL_REQUIRED)
    @Email(message = ErrorMessages.USER_EMAIL_NOT_VALID)
    private String email;

    @NotBlank(message = ErrorMessages.USER_PASSWORD_REQUIRED)
    private String password;

    @NotNull
    private Role role;
}
