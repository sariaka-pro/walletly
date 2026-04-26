package com.smartbudget.smartbudget_backend.dto.admin;

import java.math.BigDecimal;
import java.time.LocalDate;

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
public class AdminExpenseDto {
    private Long id;
    private BigDecimal amount;
    private String description;
    private LocalDate date;
    private Long userId;
    private String userEmail;
    private Long categoryId;
    private String categoryName;
    private Long budgetId;
}
