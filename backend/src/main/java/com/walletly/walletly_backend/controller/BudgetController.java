package com.walletly.walletly_backend.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.walletly.walletly_backend.exception.ErrorMessages;
import com.walletly.walletly_backend.exception.ForbiddenException;
import com.walletly.walletly_backend.model.Budget;
import com.walletly.walletly_backend.model.User;
import com.walletly.walletly_backend.service.BudgetService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    @Autowired
    private BudgetService budgetService; 


    // créer un budget
    @PostMapping("")
    public ResponseEntity<Budget> createBudget(@Valid @RequestBody Budget budget, @AuthenticationPrincipal User currentUser) {
        Budget createBudget = budgetService.createBudget(budget, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createBudget);
    }

    // récupérer toutes les dépenses 
    @GetMapping("")
    public ResponseEntity<List<Budget>> getAllBudgets(@AuthenticationPrincipal User currentUser) {
        List<Budget> budgets = budgetService.getAllBudgetsByUser(currentUser.getId()); 
        return ResponseEntity.ok(budgets); 
    }

    // récupérer le budget du mois actuel 
    @GetMapping("/current-month")
    public ResponseEntity<Budget> getCurrentMonthBudget(@AuthenticationPrincipal User currentUser) {
        Budget budget = budgetService.getCurrentMonthBudget(currentUser.getId()); 
        return ResponseEntity.ok(budget); 
    }
    
    // récupérer un budget par ID 
    @GetMapping("/{id}")
    public ResponseEntity<Budget> getBudgetById(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        Budget budget = budgetService.getBudgetById(id); 
        if (!budget.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException(ErrorMessages.BUDGET_ACCESS_DENIED);
    }
        return ResponseEntity.ok(budget);
    }

    // modifie le budget 
    @PutMapping("/{id}")
    public ResponseEntity<Budget> updateBudget(@PathVariable Long id, @Valid @RequestBody Budget budget, @AuthenticationPrincipal User currentUser) {
        Budget updated = budgetService.updateBudget(id, budget, currentUser.getId()); 
        return ResponseEntity.ok(updated); 
    }

    // supprimer un budget 
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        budgetService.deleteBudget(id, currentUser.getId());
        return ResponseEntity.noContent().build(); 
    }

    // ============ ENDPOINTS LOGIQUE MÉTIER ============

    // Récupérer l'argent restant 
    @GetMapping("/{id}/remaining")
    public ResponseEntity<BigDecimal> getRemaining(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        Budget budget = budgetService.getBudgetById(id);
        if (!budget.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException(ErrorMessages.BUDGET_ACCESS_DENIED);
        }
        BigDecimal remaining = budgetService.getRemaining(budget); 
        return ResponseEntity.ok(remaining); 
    }

    // récupère le pourcentage dépensé 
    @GetMapping("/{id}/percentage")
    public ResponseEntity<BigDecimal> getPercentageSpent(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        Budget budget = budgetService.getBudgetById(id);
        if (!budget.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException(ErrorMessages.BUDGET_ACCESS_DENIED);
        }
        BigDecimal percentage = budgetService.getPercentageSpent(budget); 
        return ResponseEntity.ok(percentage); 
    }

    // vérifie les alertes 
    @GetMapping("/{id}/alert")
    public ResponseEntity<String> checkBudgetAlert(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        Budget budget = budgetService.getBudgetById(id);
        if (!budget.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException(ErrorMessages.BUDGET_ACCESS_DENIED);
        }
        String alert = budgetService.checkBudgetAlert(budget); 
        return ResponseEntity.ok(alert); 
    }
}
