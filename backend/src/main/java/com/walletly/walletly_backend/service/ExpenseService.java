package com.walletly.walletly_backend.service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.walletly.walletly_backend.exception.BadRequestException;
import com.walletly.walletly_backend.exception.ErrorMessages;
import com.walletly.walletly_backend.exception.ForbiddenException;
import com.walletly.walletly_backend.exception.NotFoundException;
import com.walletly.walletly_backend.model.Budget;
import com.walletly.walletly_backend.model.Category;
import com.walletly.walletly_backend.model.Expense;
import com.walletly.walletly_backend.model.User;
import com.walletly.walletly_backend.repository.CategoryRepository;
import com.walletly.walletly_backend.repository.ExpenseRepository;

@Service // On dit à Spring : “Cette classe contient la logique métier liée aux
         // dépenses.”
public class ExpenseService {

    // Déclaration des repositories (accès base de données)
    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    
    @Autowired
    @Lazy // PBBB =  se crée uniquement quand le service est utilisé 
    private BudgetService budgetService; 

    @Autowired
    private InputSanitizer inputSanitizer;

    /// Injection des repositories via le constructeur
    /// Spring injecte automatiquement les dépendances
    public ExpenseService(ExpenseRepository expenseRepository,
            CategoryRepository categoryRepository,@Lazy BudgetService budgetService) {
        this.expenseRepository = expenseRepository;
        this.categoryRepository = categoryRepository;
        this.budgetService = budgetService;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }

    // 1️ - Récupérer toutes les dépenses
    public List<Expense> getAllExpenses() {
        User currentUser = getCurrentUser();
        return expenseRepository.findByUser(currentUser);
    }

    // Variante explicite utilisée par le contrôleur pour garantir le scope user
    public List<Expense> getAllExpensesByUser(User currentUser) {
        return expenseRepository.findByUser(currentUser);
    }

    // 2 - Créer une dépense
    public Expense createExpense(Expense newExpenseData) {
    /// Vérification : une catégorie est obligatoire
    if (newExpenseData.getCategory() == null
            || newExpenseData.getCategory().getId() == null) {
        throw new BadRequestException(ErrorMessages.CATEGORY_REQUIRED);
    }

    Long categoryId = newExpenseData.getCategory().getId();

    /// Vérification : la catégorie existe bien en base
    Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new NotFoundException(ErrorMessages.CATEGORY_NOT_FOUND));

    User currentUser = getCurrentUser();

    /// ✅ Vérifier que la catégorie appartient à l'utilisateur courant
    if (!category.getUser().getId().equals(currentUser.getId())) {
        throw new ForbiddenException(ErrorMessages.ACCESS_DENIED);
    }

    /// ✅ Vérifier si le budget est obligatoire
    if (newExpenseData.getBudget() == null || newExpenseData.getBudget().getId() == null) {
        throw new BadRequestException("Budget is required");
    }
    
    /// ✅ Récupérer et valider le budget
    Budget budget = budgetService.getBudgetById(newExpenseData.getBudget().getId());

    /// ✅ CORRECTED : Vérifier que le budget appartient à l'utilisateur (avec ! pour négation)
    if (!budget.getUser().getId().equals(currentUser.getId())) {
        throw new ForbiddenException(ErrorMessages.BUDGET_ACCESS_DENIED);
    }

    /// On associe les vraies entités
    newExpenseData.setDescription(inputSanitizer.sanitizePlainText(newExpenseData.getDescription(), "expense.description"));
    newExpenseData.setCategory(category);
    newExpenseData.setUser(currentUser);
    newExpenseData.setBudget(budget);

    /// ✅ Sauvegarder la dépense
    Expense savedExpense = expenseRepository.save(newExpenseData);

    /// ✅ METTRE À JOUR LE BUDGET DU MOIS (une seule fois, direct)
    budgetService.updateBudgetSpent(budget);

    return savedExpense;
}

    // 3️ - Récupérer une dépense par id
    public Expense getExpenseById(Long id) {

        // 1 Récupérer l'expense ou 404
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.EXPENSE_NOT_FOUND));

        // 2️Récupérer le user connecté
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = (User) authentication.getPrincipal();

        // 3 Vérifier que la dépense appartient au user
        if (!expense.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException(ErrorMessages.ACCESS_DENIED);
        }

        // 4 Retourner la dépense
        return expense;
    }

    // 4️ - Mettre à jour une dépense
    public Expense updateExpense(Long id, Expense newExpenseData) {

        /// On va chercher la dépense existante
        Expense existingExpense = expenseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.EXPENSE_NOT_FOUND));

        /// Vérification : la dépense appartient bien au user connecté
        User currentUser = getCurrentUser();

        if (!existingExpense.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException(ErrorMessages.ACCESS_DENIED);
        }

        /// Vérification : la catégorie est obligatoire
        if (newExpenseData.getCategory() == null
                || newExpenseData.getCategory().getId() == null) {
            throw new BadRequestException(ErrorMessages.CATEGORY_REQUIRED);
        }

        /// On vérifie que la catégorie existe
        Long categoryId = newExpenseData.getCategory().getId();

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.CATEGORY_NOT_FOUND));

        /// vérifie que la categorie appartient à l'utilisateur 
        if(!category.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException(ErrorMessages.ACCESS_DENIED);
        }

        /// Mise à jour des champs
        existingExpense.setAmount(newExpenseData.getAmount());
        existingExpense.setDate(newExpenseData.getDate());
        existingExpense.setDescription(inputSanitizer.sanitizePlainText(newExpenseData.getDescription(), "expense.description"));
        existingExpense.setCategory(category);

        // Récupérer l'ancien mois et la nouvelle date
        int year = newExpenseData.getDate().getYear();
        int month = newExpenseData.getDate().getMonthValue();

        // Sauvegarder
        Expense updated = expenseRepository.save(existingExpense);

        // METTRE À JOUR LE BUDGET DU MOIS
        updateBudgetAfterExpenseChange(currentUser.getId(), year, month);

        return updated;
    }

    // 5️ - Supprimer une dépense
    public void deleteExpense(Long id) {

        /// On vérifie que la dépense existe
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.EXPENSE_NOT_FOUND));

        /// 🔐 Vérifier que la dépense appartient au user connecté
        User currentUser = getCurrentUser();

        if (!expense.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException(ErrorMessages.ACCESS_DENIED);
        }
        // Avant de supprimer, récupérer la date
        int year = expense.getDate().getYear();
        int month = expense.getDate().getMonthValue();

        // Supprimer
        expenseRepository.delete(expense);

        // METTRE À JOUR LE BUDGET DU MOIS
        updateBudgetAfterExpenseChange(currentUser.getId(), year, month);
    }

    // ============ LOGIQUE MÉTIER POUR BUDGET ============
    /**
     * Récupérer le total des dépenses d'un utilisateur pour un mois spécifique
     * Utilisé par BudgetService pour calculer currentSpent
     */
    public BigDecimal getTotalByUserAndMonth(Long userId, YearMonth yearMonth) {
        // ✅ Passer year et month séparément
    List<Expense> expenses = expenseRepository.findByUserAndYearMonth(userId, yearMonth.getYear(), yearMonth.getMonthValue());  
        return expenses.stream()
            .map(Expense::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /// Private, usage en interne 
    private void updateBudgetAfterExpenseChange(Long userId, int year, int month) {
        try {
            YearMonth yearMonth = YearMonth.of(year, month); 
            Budget budget = budgetService.getBudgetByYearMonth(userId, yearMonth);
            budgetService.updateBudgetSpent(budget);

            String alert = budgetService.checkBudgetAlert(budget);
            if(alert != null) {
                System.out.println("Budget alert: " + alert);
            }
            
        } catch(NotFoundException e) {
            // Le budget n'existe pas pour ce mois, ce n'est pas grave
            System.out.println("⚠️ Budget not found for " + year + "-" + month);
        }
    }
}
