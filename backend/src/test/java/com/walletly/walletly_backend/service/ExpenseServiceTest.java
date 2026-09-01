package com.walletly.walletly_backend.service;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.walletly.walletly_backend.model.Budget;
import com.walletly.walletly_backend.model.Category;
import com.walletly.walletly_backend.model.Expense;
import com.walletly.walletly_backend.model.User;
import com.walletly.walletly_backend.repository.CategoryRepository;
import com.walletly.walletly_backend.repository.ExpenseRepository;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BudgetService budgetService;

    @Mock
    private InputSanitizer inputSanitizer;

    @InjectMocks
    private ExpenseService expenseService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void updateExpenseRecalculatesOldAndNewBudgetsWhenBudgetChanges() {
        User user = User.builder().id(1L).email("user@example.com").build();
        Category category = Category.builder().id(5L).name("Transport").user(user).build();
        Budget januaryBudget = Budget.builder().id(10L).yearMonth(YearMonth.of(2026, 1)).user(user).build();
        Budget februaryBudget = Budget.builder().id(11L).yearMonth(YearMonth.of(2026, 2)).user(user).build();
        Expense existing = Expense.builder()
            .id(100L)
            .amount(BigDecimal.valueOf(40))
            .description("Train")
            .date(LocalDate.of(2026, 1, 15))
            .category(category)
            .budget(januaryBudget)
            .user(user)
            .build();
        Expense update = Expense.builder()
            .amount(BigDecimal.valueOf(50))
            .description("Train retour")
            .date(LocalDate.of(2026, 2, 15))
            .category(Category.builder().id(5L).build())
            .budget(Budget.builder().id(11L).build())
            .build();
        authenticate(user);
        when(expenseRepository.findById(100L)).thenReturn(Optional.of(existing));
        when(categoryRepository.findById(5L)).thenReturn(Optional.of(category));
        when(budgetService.getBudgetById(11L)).thenReturn(februaryBudget);
        when(inputSanitizer.sanitizePlainText("Train retour", "expense.description"))
            .thenReturn("Train retour");
        when(expenseRepository.save(existing)).thenReturn(existing);

        Expense result = expenseService.updateExpense(100L, update);

        assertSame(februaryBudget, result.getBudget());
        verify(budgetService).updateBudgetSpent(februaryBudget);
        verify(budgetService).updateBudgetSpent(januaryBudget);
    }

    @Test
    void updateExpenseRecalculatesBudgetOnlyOnceWhenItDoesNotChange() {
        User user = User.builder().id(1L).email("user@example.com").build();
        Category category = Category.builder().id(5L).name("Transport").user(user).build();
        Budget budget = Budget.builder().id(10L).yearMonth(YearMonth.of(2026, 1)).user(user).build();
        Expense existing = Expense.builder()
            .id(100L).amount(BigDecimal.TEN).description("Bus")
            .date(LocalDate.of(2026, 1, 10)).category(category).budget(budget).user(user).build();
        Expense update = Expense.builder()
            .amount(BigDecimal.valueOf(15)).description("Bus aller-retour")
            .date(LocalDate.of(2026, 1, 10))
            .category(Category.builder().id(5L).build())
            .budget(Budget.builder().id(10L).build()).build();
        authenticate(user);
        when(expenseRepository.findById(100L)).thenReturn(Optional.of(existing));
        when(categoryRepository.findById(5L)).thenReturn(Optional.of(category));
        when(budgetService.getBudgetById(10L)).thenReturn(budget);
        when(inputSanitizer.sanitizePlainText("Bus aller-retour", "expense.description"))
            .thenReturn("Bus aller-retour");
        when(expenseRepository.save(existing)).thenReturn(existing);

        expenseService.updateExpense(100L, update);

        verify(budgetService).updateBudgetSpent(budget);
        verify(budgetService, never()).getBudgetByYearMonth(user.getId(), budget.getYearMonth());
    }

    private void authenticate(User user) {
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(user, null, List.of())
        );
    }
}
