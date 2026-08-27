package com.walletly.walletly_backend.controller;

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

import com.walletly.walletly_backend.model.SavingsGoal;
import com.walletly.walletly_backend.model.User;
import com.walletly.walletly_backend.service.SavingsGoalService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/savings-goals")
public class SavingsGoalController {

    @Autowired
    private SavingsGoalService savingsGoalService;

    // Créer un nouveau savings goal
    @PostMapping("")
    public ResponseEntity<SavingsGoal> createSavingsGoal(
            @Valid @RequestBody SavingsGoal savingsGoal,
            @AuthenticationPrincipal User currentUser) {
        SavingsGoal created = savingsGoalService.createSavingsGoal(savingsGoal, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Récupérer tous les savings goals de l'utilisateur connecté
    @GetMapping("")
    public ResponseEntity<List<SavingsGoal>> getAllSavingsGoals(@AuthenticationPrincipal User currentUser) {
        List<SavingsGoal> goals = savingsGoalService.getAllByUser(currentUser.getId());
        return ResponseEntity.ok(goals);
    }

    // Récupérer un savings goal par ID
    @GetMapping("/{id}")
    public ResponseEntity<SavingsGoal> getSavingsGoalById(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        SavingsGoal goal = savingsGoalService.getById(id);
        return ResponseEntity.ok(goal);
    }

    // Modifier un savings goal
    @PutMapping("/{id}")
    public ResponseEntity<SavingsGoal> updateSavingsGoal(
            @PathVariable Long id,
            @Valid @RequestBody SavingsGoal savingsGoal,
            @AuthenticationPrincipal User currentUser) {
        SavingsGoal updated = savingsGoalService.updateSavingsGoal(id, savingsGoal, currentUser.getId());
        return ResponseEntity.ok(updated);
    }

    // Supprimer un savings goal
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSavingsGoal(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        savingsGoalService.deleteSavingsGoal(id, currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}
