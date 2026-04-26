package com.smartbudget.smartbudget_backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartbudget.smartbudget_backend.dto.admin.AdminExpenseDto;
import com.smartbudget.smartbudget_backend.dto.admin.AdminGlobalStatsDto;
import com.smartbudget.smartbudget_backend.dto.admin.AdminUserDetailsDto;
import com.smartbudget.smartbudget_backend.dto.admin.AdminUserSummaryDto;
import com.smartbudget.smartbudget_backend.exception.NotFoundException;
import com.smartbudget.smartbudget_backend.model.Expense;
import com.smartbudget.smartbudget_backend.model.User;
import com.smartbudget.smartbudget_backend.model.enums.Role;
import com.smartbudget.smartbudget_backend.repository.BudgetRepository;
import com.smartbudget.smartbudget_backend.repository.CategoryRepository;
import com.smartbudget.smartbudget_backend.repository.ExpenseRepository;
import com.smartbudget.smartbudget_backend.repository.UserRepository;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final BudgetRepository budgetRepository;

    public AdminService(
        UserRepository userRepository,
        ExpenseRepository expenseRepository,
        CategoryRepository categoryRepository,
        BudgetRepository budgetRepository
    ) {
        this.userRepository = userRepository;
        this.expenseRepository = expenseRepository;
        this.categoryRepository = categoryRepository;
        this.budgetRepository = budgetRepository;
    }

    // Retourne les statistiques globales visibles sur le dashboard ADMIN.
    public AdminGlobalStatsDto getGlobalStats() {
        return AdminGlobalStatsDto.builder()
            .totalUsers(userRepository.count())
            .totalExpensesAmount(expenseRepository.getTotalExpensesAmount())
            .totalCategories(categoryRepository.count())
            .build();
    }

    // Retourne la liste de tous les utilisateurs avec un résumé de leurs dépenses.
    public List<AdminUserSummaryDto> getAllUsers() {
        return userRepository.findAll().stream()
            .map(user -> AdminUserSummaryDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .totalExpensesAmount(expenseRepository.getTotalExpensesAmountByUserId(user.getId()))
                .build())
            .toList();
    }

    // Retourne le détail complet d'un utilisateur pour l'écran d'administration.
    public AdminUserDetailsDto getUserDetails(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found"));

        return AdminUserDetailsDto.builder()
            .id(user.getId())
            .email(user.getEmail())
            .role(user.getRole())
            .totalExpensesCount(expenseRepository.countByUser_Id(userId))
            .totalBudgets(budgetRepository.countByUser_Id(userId))
            .totalCategories(categoryRepository.countByUser_Id(userId))
            .totalExpensesAmount(expenseRepository.getTotalExpensesAmountByUserId(userId))
            .build();
    }

    // Retourne toutes les dépenses de la plateforme dans un format adapté à l'admin.
    public List<AdminExpenseDto> getAllExpenses() {
        return expenseRepository.findAll().stream()
            .map(this::toAdminExpenseDto)
            .toList();
    }

    @Transactional
    // Change le rôle d'un utilisateur (USER/ADMIN).
    public void changeUserRole(Long userId, Role role) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found"));

        user.setRole(role);
        userRepository.save(user);
    }

    @Transactional
    // Supprime un utilisateur et ses données dépendantes pour éviter les conflits FK.
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found"));

        List<Expense> expenses = expenseRepository.findByUser_Id(userId);
        if (!expenses.isEmpty()) {
            expenseRepository.deleteAll(expenses);
        }

        budgetRepository.deleteAll(budgetRepository.findByUser_Id(userId));
        categoryRepository.deleteAll(categoryRepository.findByUser(user));
        userRepository.delete(user);
    }

    // Transforme l'entité Expense en DTO admin sans exposer l'entité complète.
    private AdminExpenseDto toAdminExpenseDto(Expense expense) {
        return AdminExpenseDto.builder()
            .id(expense.getId())
            .amount(expense.getAmount())
            .description(expense.getDescription())
            .date(expense.getDate())
            .userId(expense.getUser() != null ? expense.getUser().getId() : null)
            .userEmail(expense.getUser() != null ? expense.getUser().getEmail() : null)
            .categoryId(expense.getCategory() != null ? expense.getCategory().getId() : null)
            .categoryName(expense.getCategory() != null ? expense.getCategory().getName() : null)
            .budgetId(expense.getBudget() != null ? expense.getBudget().getId() : null)
            .build();
    }
}
