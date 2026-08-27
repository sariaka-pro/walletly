package com.walletly.walletly_backend.dto.admin;

import java.math.BigDecimal;

import com.walletly.walletly_backend.model.enums.Role;

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
public class AdminUserSummaryDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
    private BigDecimal totalExpensesAmount;
}
