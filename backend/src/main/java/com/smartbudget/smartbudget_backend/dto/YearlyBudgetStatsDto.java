package com.smartbudget.smartbudget_backend.dto;

import java.math.BigDecimal;
import java.util.List;

import com.smartbudget.smartbudget_backend.model.Budget;

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
public class YearlyBudgetStatsDto {
    private BigDecimal totalLimit;
    private BigDecimal totalSpent;
    private BigDecimal totalRemaining;
    private BigDecimal percentageSpent;
    private List<Budget> monthlyBreakdown;
}
