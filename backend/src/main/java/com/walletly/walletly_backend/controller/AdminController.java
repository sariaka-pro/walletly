package com.walletly.walletly_backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.walletly.walletly_backend.dto.admin.AdminBudgetDto;
import com.walletly.walletly_backend.dto.admin.AdminExpenseDto;
import com.walletly.walletly_backend.dto.admin.AdminGlobalStatsDto;
import com.walletly.walletly_backend.dto.admin.AdminUserDetailsDto;
import com.walletly.walletly_backend.dto.admin.AdminUserSummaryDto;
import com.walletly.walletly_backend.dto.admin.CreateAdminUserRequest;
import com.walletly.walletly_backend.dto.admin.RoleChangeRequest;
import com.walletly.walletly_backend.dto.admin.UpdateAdminUserRequest;
import com.walletly.walletly_backend.service.AdminService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // Expose les stats globales pour la vue overview ADMIN.
    @GetMapping("/stats")
    public ResponseEntity<AdminGlobalStatsDto> getGlobalStats() {
        return ResponseEntity.ok(adminService.getGlobalStats());
    }

    // Expose la liste de tous les utilisateurs.
    @GetMapping("/users")
    public ResponseEntity<List<AdminUserSummaryDto>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    // Cree un utilisateur depuis le panneau admin.
    @PostMapping("/users")
    public ResponseEntity<AdminUserSummaryDto> createUser(@Valid @RequestBody CreateAdminUserRequest request) {
        return ResponseEntity.status(201).body(adminService.createUser(request));
    }

    // Expose le détail d'un utilisateur spécifique.
    @GetMapping("/users/{id}")
    public ResponseEntity<AdminUserDetailsDto> getUserDetails(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getUserDetails(id));
    }

    // Met a jour un utilisateur (email, mot de passe optionnel, role).
    @PutMapping("/users/{id}")
    public ResponseEntity<AdminUserSummaryDto> updateUser(
        @PathVariable Long id,
        @Valid @RequestBody UpdateAdminUserRequest request
    ) {
        return ResponseEntity.ok(adminService.updateUser(id, request));
    }

    // Permet de changer le rôle d'un utilisateur.
    @PutMapping("/users/{id}/role")
    public ResponseEntity<Void> changeUserRole(@PathVariable Long id, @Valid @RequestBody RoleChangeRequest request) {
        adminService.changeUserRole(id, request.getRole());
        return ResponseEntity.noContent().build();
    }

    // Permet de supprimer un utilisateur depuis l'admin.
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // Expose toutes les dépenses de tous les utilisateurs.
    @GetMapping("/expenses")
    public ResponseEntity<List<AdminExpenseDto>> getAllExpenses() {
        return ResponseEntity.ok(adminService.getAllExpenses());
    }

    // Expose tous les budgets de tous les utilisateurs.
    @GetMapping("/budgets")
    public ResponseEntity<List<AdminBudgetDto>> getAllBudgets() {
        return ResponseEntity.ok(adminService.getAllBudgets());
    }

}
