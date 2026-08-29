package com.walletly.walletly_backend.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.walletly.walletly_backend.exception.BadRequestException;
import com.walletly.walletly_backend.exception.ErrorMessages;
import com.walletly.walletly_backend.exception.ForbiddenException;
import com.walletly.walletly_backend.exception.NotFoundException;
import com.walletly.walletly_backend.model.Budget;
import com.walletly.walletly_backend.model.User;
import com.walletly.walletly_backend.model.enums.BudgetPeriod;
import com.walletly.walletly_backend.repository.BudgetRepository;

@Service
public class BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private AuthService authService;

    @Autowired
    private InputSanitizer inputSanitizer;

    // ============ CRUD DE BASE ============

    /**
     * Créer un nouveau budget
     */
    public Budget createBudget(Budget budget, Long userId) {
        budget.setName(inputSanitizer.sanitizePlainText(budget.getName(), "budget.name"));

        // 1. Vérifier que le budget n'existe pas déjà pour ce mois
        if (budgetRepository.existsByUser_IdAndYearMonth(userId, budget.getYearMonth())) {
            throw new BadRequestException(ErrorMessages.BUDGET_ALREADY_EXISTS + budget.getYearMonth());
        }

        // 2. Récupérer l'utilisateur
        User user = authService.getUserById(userId);

        // 3. Assigner l'utilisateur et initialiser currentSpent
        budget.setUser(user);
        budget.setCurrentSpent(BigDecimal.ZERO);

        // 4. Sauvegarder et retourner
        return budgetRepository.save(budget);
    }

    /**
     * Récupérer un budget par ID
     */
    public Budget getBudgetById(Long budgetId) {
        return budgetRepository.findById(budgetId)
            .orElseThrow(() -> new NotFoundException(ErrorMessages.BUDGET_NOT_FOUND));
    }

    /**
     * Récupérer tous les budgets d'un utilisateur
     */
    public List<Budget> getAllBudgetsByUser(Long userId) {
        return budgetRepository.findByUser_Id(userId);
    }

    /**
     * Récupérer le budget du mois actuel (ou le créer s'il n'existe pas)
     */
    public Budget getCurrentMonthBudget(Long userId) {
        YearMonth now = YearMonth.now();

        Optional<Budget> budget = budgetRepository.findByUser_IdAndYearMonth(userId, now);

        if (budget.isPresent()) {
            return budget.get();
        }

        // Créer automatiquement le budget du mois
        User user = authService.getUserById(userId);
        Budget newBudget = Budget.builder()
            .name(now.toString())
            .spendingLimit(BigDecimal.valueOf(30000.0))
            .currentSpent(BigDecimal.ZERO)
            .period(BudgetPeriod.MONTHLY)
            .yearMonth(now)
            .user(user)
            .build();

        return budgetRepository.save(newBudget);
    }

    /**
     * Modifier un budget
     */
    public Budget updateBudget(Long budgetId, Budget budgetUpdated, Long userId) {
        Budget budget = budgetRepository.findById(budgetId)
            .orElseThrow(() -> new NotFoundException(ErrorMessages.BUDGET_NOT_FOUND));

        // Sécurité : vérifier que c'est le bon utilisateur
        if (!budget.getUser().getId().equals(userId)) {
            throw new ForbiddenException(ErrorMessages.BUDGET_ACCESS_DENIED);
        }

        // Update seulement les champs autorisés (pas currentSpent !)
        budget.setName(inputSanitizer.sanitizePlainText(budgetUpdated.getName(), "budget.name"));
        budget.setSpendingLimit(budgetUpdated.getSpendingLimit());

        return budgetRepository.save(budget);
    }

    /**
     * Supprimer un budget
     */
    public void deleteBudget(Long budgetId, Long userId) {
        Budget budget = budgetRepository.findById(budgetId)
            .orElseThrow(() -> new NotFoundException(ErrorMessages.BUDGET_NOT_FOUND));

        // Sécurité : vérifier que c'est le bon utilisateur
        if (!budget.getUser().getId().equals(userId)) {
            throw new ForbiddenException(ErrorMessages.BUDGET_ACCESS_DENIED);
        }

        budgetRepository.delete(budget);
    }

    public Budget getBudgetByYearMonth(Long userId, YearMonth yearMonth) {
        return budgetRepository.findByUser_IdAndYearMonth(userId, yearMonth)
            .orElseThrow(() -> new NotFoundException(ErrorMessages.BUDGET_NOT_FOUND));
    }
    
    // ============ LOGIQUE MÉTIER ============

    /**
     * Mettre à jour les dépenses du budget (appelé quand une dépense est ajoutée/supprimée)
     */
    public void updateBudgetSpent(Budget budget) {
        BigDecimal totalSpent = expenseService.getTotalByUserAndMonth(
            budget.getUser().getId(),
            budget.getYearMonth()
        );
        budget.setCurrentSpent(totalSpent);
        budgetRepository.save(budget);
    }

    /**
     * Calculer l'argent restant dans le budget
     */
    public BigDecimal getRemaining(Budget budget) {
        return budget.getSpendingLimit().subtract(budget.getCurrentSpent());    }

    /**
     * Calculer le pourcentage dépensé du budget
     */
   public BigDecimal getPercentageSpent(Budget budget) {
    if (budget.getSpendingLimit().compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
    return budget.getCurrentSpent()
                 .divide(budget.getSpendingLimit(), 2, RoundingMode.HALF_UP)
                 .multiply(BigDecimal.valueOf(100));
}

    /**
     * Vérifier les alertes du budget
     */
    public String checkBudgetAlert(Budget budget) {
        BigDecimal percentageSpent = getPercentageSpent(budget);
    
        if (percentageSpent.compareTo(BigDecimal.valueOf(100)) >= 0) {
            return "🚨 ALERTE : Vous avez dépassé votre budget !";
        } else if (percentageSpent.compareTo(BigDecimal.valueOf(80)) >= 0) {
            return "⚠️ ATTENTION : Vous avez dépensé 80% de votre budget";
        } else if (percentageSpent.compareTo(BigDecimal.valueOf(50)) >= 0) {
            return "ℹ️ INFO : Vous avez dépensé 50% de votre budget";
        } else if (percentageSpent.compareTo(BigDecimal.valueOf(30)) >= 0) {
            return "💡 Vous avez dépensé 30% de votre budget";
        }
    
        return null;
    }
    }