package com.smartbudget.smartbudget_backend.dto.admin;

import com.smartbudget.smartbudget_backend.model.enums.Role;

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
public class RoleChangeRequest {

    @NotNull(message = "Role is required")
    private Role role;
}
