package com.walletly.walletly_backend.dto.admin;

import java.math.BigDecimal;

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
public class AdminBudgetDto {
    private Long id;
    private String name;
    private BigDecimal spendingLimit;
    private BigDecimal currentSpent;
    private String yearMonth;
    private Long userId;
    private String userEmail;
}
