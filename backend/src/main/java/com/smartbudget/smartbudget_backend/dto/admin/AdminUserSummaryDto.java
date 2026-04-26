package com.smartbudget.smartbudget_backend.dto.admin;

import java.math.BigDecimal;

import com.smartbudget.smartbudget_backend.model.enums.Role;

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
    private String email;
    private Role role;
    private BigDecimal totalExpensesAmount;
}
