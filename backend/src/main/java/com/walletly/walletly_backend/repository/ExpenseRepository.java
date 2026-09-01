package com.walletly.walletly_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.walletly.walletly_backend.model.Expense;
import com.walletly.walletly_backend.model.User;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUser(User user);

    // Récupère toutes les dépenses d'un utilisateur (par ID).
    List<Expense> findByUser_Id(Long userId);

    // Compte le nombre de dépenses d'un utilisateur.
    long countByUser_Id(Long userId);

    boolean existsByBudget_Id(Long budgetId);

    // Retourne le montant total des dépenses de la plateforme.
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e")
    java.math.BigDecimal getTotalExpensesAmount();

    // Retourne le montant total des dépenses pour un utilisateur donné.
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user.id = :userId")
    java.math.BigDecimal getTotalExpensesAmountByUserId(@Param("userId") Long userId);

    // Récupérer toutes les dépenses d'un utilisateur pour un mois spécifique
    // ✅ APRÈS
    @Query("""
        SELECT e FROM Expense e 
        WHERE e.user.id = :userId 
        AND YEAR(e.date) = :year 
        AND MONTH(e.date) = :month""")

    List<Expense> findByUserAndYearMonth(
        @Param("userId") Long userId,
        @Param("year") int year,
        @Param("month") int month
);
}
