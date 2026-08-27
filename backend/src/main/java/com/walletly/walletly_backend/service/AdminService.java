package com.walletly.walletly_backend.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.walletly.walletly_backend.dto.admin.AdminBudgetDto;
import com.walletly.walletly_backend.dto.admin.AdminExpenseDto;
import com.walletly.walletly_backend.dto.admin.AdminGlobalStatsDto;
import com.walletly.walletly_backend.dto.admin.AdminSavingsGoalDto;
import com.walletly.walletly_backend.dto.admin.AdminUserDetailsDto;
import com.walletly.walletly_backend.dto.admin.AdminUserSummaryDto;
import com.walletly.walletly_backend.dto.admin.CreateAdminUserRequest;
import com.walletly.walletly_backend.dto.admin.UpdateAdminUserRequest;
import com.walletly.walletly_backend.exception.BadRequestException;
import com.walletly.walletly_backend.exception.ErrorMessages;
import com.walletly.walletly_backend.exception.NotFoundException;
import com.walletly.walletly_backend.model.Budget;
import com.walletly.walletly_backend.model.Expense;
import com.walletly.walletly_backend.model.SavingsGoal;
import com.walletly.walletly_backend.model.User;
import com.walletly.walletly_backend.model.enums.Role;
import com.walletly.walletly_backend.repository.BudgetRepository;
import com.walletly.walletly_backend.repository.CategoryRepository;
import com.walletly.walletly_backend.repository.ExpenseRepository;
import com.walletly.walletly_backend.repository.SavingsGoalRepository;
import com.walletly.walletly_backend.repository.UserRepository;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final BudgetRepository budgetRepository;
    private final SavingsGoalRepository savingsGoalRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(
        UserRepository userRepository,
        ExpenseRepository expenseRepository,
        CategoryRepository categoryRepository,
        BudgetRepository budgetRepository,
        SavingsGoalRepository savingsGoalRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.expenseRepository = expenseRepository;
        this.categoryRepository = categoryRepository;
        this.budgetRepository = budgetRepository;
        this.savingsGoalRepository = savingsGoalRepository;
        this.passwordEncoder = passwordEncoder;
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
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .totalExpensesAmount(expenseRepository.getTotalExpensesAmountByUserId(user.getId()))
                .build())
            .toList();
    }

    // Cree un nouvel utilisateur avec le role choisi depuis l'interface admin.
    public AdminUserSummaryDto createUser(CreateAdminUserRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new BadRequestException(ErrorMessages.USER_EMAIL_ALREADY_EXISTS);
        }

        User user = User.builder()
            .firstName(request.getFirstName().trim())
            .lastName(request.getLastName().trim())
            .email(normalizedEmail)
            .password(passwordEncoder.encode(request.getPassword()))
            .role(request.getRole())
            .build();

        User savedUser;
        try {
            savedUser = userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            // Handles race conditions and DB unique constraints gracefully.
            throw new BadRequestException(ErrorMessages.USER_EMAIL_ALREADY_EXISTS);
        }

        return AdminUserSummaryDto.builder()
            .id(savedUser.getId())
            .firstName(savedUser.getFirstName())
            .lastName(savedUser.getLastName())
            .email(savedUser.getEmail())
            .role(savedUser.getRole())
            .totalExpensesAmount(BigDecimal.ZERO)
            .build();
    }

    // Met a jour email, mot de passe optionnel et role d'un utilisateur.
    @Transactional
    public AdminUserSummaryDto updateUser(Long userId, UpdateAdminUserRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found"));

        String normalizedEmail = request.getEmail().trim().toLowerCase(Locale.ROOT);
        if (!normalizedEmail.equals(user.getEmail()) && userRepository.existsByEmail(normalizedEmail)) {
            throw new BadRequestException(ErrorMessages.USER_EMAIL_ALREADY_EXISTS);
        }

        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setEmail(normalizedEmail);
        user.setRole(request.getRole());

        String rawPassword = request.getPassword();
        if (rawPassword != null && !rawPassword.trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(rawPassword));
        }

        User savedUser;
        try {
            savedUser = userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException(ErrorMessages.USER_EMAIL_ALREADY_EXISTS);
        }

        return AdminUserSummaryDto.builder()
            .id(savedUser.getId())
            .firstName(savedUser.getFirstName())
            .lastName(savedUser.getLastName())
            .email(savedUser.getEmail())
            .role(savedUser.getRole())
            .totalExpensesAmount(expenseRepository.getTotalExpensesAmountByUserId(savedUser.getId()))
            .build();
    }

    // Retourne le détail complet d'un utilisateur pour l'écran d'administration.
    public AdminUserDetailsDto getUserDetails(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found"));

        return AdminUserDetailsDto.builder()
            .id(user.getId())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
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

    // Retourne tous les budgets de la plateforme dans un format adapté à l'admin.
    public List<AdminBudgetDto> getAllBudgets() {
        return budgetRepository.findAll().stream()
            .map(this::toAdminBudgetDto)
            .toList();
    }

    // Retourne tous les objectifs d'epargne de la plateforme dans un format adapté à l'admin.
    public List<AdminSavingsGoalDto> getAllSavingsGoals() {
        return savingsGoalRepository.findAll().stream()
            .map(this::toAdminSavingsGoalDto)
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

    // Transforme l'entité Budget en DTO admin sans exposer l'entité complète.
    private AdminBudgetDto toAdminBudgetDto(Budget budget) {
        return AdminBudgetDto.builder()
            .id(budget.getId())
            .name(budget.getName())
            .spendingLimit(budget.getSpendingLimit())
            .currentSpent(budget.getCurrentSpent())
            .yearMonth(budget.getYearMonth() != null ? budget.getYearMonth().toString() : null)
            .userId(budget.getUser() != null ? budget.getUser().getId() : null)
            .userEmail(budget.getUser() != null ? budget.getUser().getEmail() : null)
            .build();
    }

    // Transforme l'entité SavingsGoal en DTO admin sans exposer l'entité complète.
    private AdminSavingsGoalDto toAdminSavingsGoalDto(SavingsGoal savingsGoal) {
        return AdminSavingsGoalDto.builder()
            .id(savingsGoal.getId())
            .name(savingsGoal.getName())
            .targetAmount(savingsGoal.getTargetAmount())
            .currentAmount(savingsGoal.getCurrentAmount())
            .deadline(savingsGoal.getDeadline())
            .userId(savingsGoal.getUser() != null ? savingsGoal.getUser().getId() : null)
            .userEmail(savingsGoal.getUser() != null ? savingsGoal.getUser().getEmail() : null)
            .build();
    }
}
