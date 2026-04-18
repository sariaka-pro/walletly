package com.smartbudget.smartbudget_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.smartbudget.smartbudget_backend.model.Expense;
import com.smartbudget.smartbudget_backend.model.User;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUser(User user);

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