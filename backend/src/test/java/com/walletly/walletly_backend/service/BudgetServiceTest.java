package com.walletly.walletly_backend.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.walletly.walletly_backend.exception.BadRequestException;
import com.walletly.walletly_backend.model.Budget;
import com.walletly.walletly_backend.model.User;
import com.walletly.walletly_backend.repository.BudgetRepository;
import com.walletly.walletly_backend.repository.ExpenseRepository;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private ExpenseService expenseService;

    @Mock
    private AuthService authService;

    @Mock
    private InputSanitizer inputSanitizer;

    @InjectMocks
    private BudgetService budgetService;

    @Test
    void deleteBudgetRejectsBudgetContainingExpenses() {
        User user = User.builder().id(1L).build();
        Budget budget = Budget.builder().id(10L).user(user).build();
        when(budgetRepository.findById(10L)).thenReturn(Optional.of(budget));
        when(expenseRepository.existsByBudget_Id(10L)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> budgetService.deleteBudget(10L, 1L));

        verify(budgetRepository, never()).delete(budget);
    }

    @Test
    void deleteBudgetDeletesEmptyOwnedBudget() {
        User user = User.builder().id(1L).build();
        Budget budget = Budget.builder().id(10L).user(user).build();
        when(budgetRepository.findById(10L)).thenReturn(Optional.of(budget));
        when(expenseRepository.existsByBudget_Id(10L)).thenReturn(false);

        budgetService.deleteBudget(10L, 1L);

        verify(budgetRepository).delete(budget);
    }
}
