package com.smartbudget.smartbudget_backend.model; 

import java.util.List;

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
public class YearlyBudgetStats {
    private Double totalLimit;
    private Double totalSpent;
    private Double totalRemaining;
    private Double percentageSpent;
    private List<Budget> monthlyBreakdown;
}