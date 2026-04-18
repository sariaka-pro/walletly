package com.smartbudget.smartbudget_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.smartbudget.smartbudget_backend.model.Budget;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;



@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByUser_Id(Long userId);

    // 2. Récupérer LE budget d'un utilisateur pour UN MOIS spécifique
    Optional<Budget> findByUser_IdAndYearMonth(Long userId, YearMonth yearMonth);

    // 3. Récupérer TOUS les budgets d'un utilisateur pour UNE ANNÉE
    @Query(value = "SELECT * FROM budgets b WHERE b.user_id = :userId AND EXTRACT(YEAR FROM b.year_month::date) = :year", nativeQuery = true)
    List<Budget> findByUserIdAndYear(@Param("userId") Long userId, @Param("year") int year);
    // 4. Vérifier si un budget existe pour un mois
    boolean existsByUser_IdAndYearMonth(Long userId, YearMonth yearMonth);
}