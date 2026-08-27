package com.walletly.walletly_backend.repository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.walletly.walletly_backend.model.Budget;



@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByUser_Id(Long userId);

    // Compte le nombre de budgets d'un utilisateur.
    long countByUser_Id(Long userId);

    // 2. Récupérer LE budget d'un utilisateur pour UN MOIS spécifique
    Optional<Budget> findByUser_IdAndYearMonth(Long userId, YearMonth yearMonth);

    // 4. Vérifier si un budget existe pour un mois
    boolean existsByUser_IdAndYearMonth(Long userId, YearMonth yearMonth);
}