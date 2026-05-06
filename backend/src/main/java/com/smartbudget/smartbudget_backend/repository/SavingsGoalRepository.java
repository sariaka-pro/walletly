package com.smartbudget.smartbudget_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smartbudget.smartbudget_backend.model.SavingsGoal;

@Repository
public interface SavingsGoalRepository extends JpaRepository<SavingsGoal, Long> {

    // Récupérer tous les savings goals d'un utilisateur
    List<SavingsGoal> findByUser_Id(Long userId);
}
