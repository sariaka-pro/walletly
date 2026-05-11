package com.smartbudget.smartbudget_backend.controller; 

import java.util.List;

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

import com.smartbudget.smartbudget_backend.model.Expense;
import com.smartbudget.smartbudget_backend.model.User;
import com.smartbudget.smartbudget_backend.service.ExpenseService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private ExpenseService expenseService; 

    /// Le constructeur du controller. Permet au controller d'utiliser toutes les méthodes du Service. 
    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping("")
    public List<Expense> getAllExpenses(@AuthenticationPrincipal User currentUser) {
        return expenseService.getAllExpensesByUser(currentUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Expense> getExpenseById(@PathVariable Long id) {
        Expense expense = expenseService.getExpenseById(id);
        return ResponseEntity.ok(expense);
    }

    @PostMapping("")
    public Expense createExpense(@Valid @RequestBody Expense expense) { /// @Valid permet de valider que tous les  champs dans Expense sont valides. 
        return expenseService.createExpense(expense);
    }

    @PutMapping("/{id}")
    public Expense updateExpense(@PathVariable Long id, @Valid @RequestBody Expense newExpenseData) {
        return expenseService.updateExpense(id, newExpenseData);
    }

    @DeleteMapping("/{id}")
    public void deletExpenses(@PathVariable Long id) {
        expenseService.deleteExpense(id);
    }

}