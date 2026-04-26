package com.smartbudget.smartbudget_backend.dto.admin;

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
public class AdminGlobalStatsDto {
    private long totalUsers;
    private BigDecimal totalExpensesAmount;
    private long totalCategories;
}
