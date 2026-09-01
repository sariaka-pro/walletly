package com.walletly.walletly_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.walletly.walletly_backend.model.SavingsGoal;

@Repository
public interface SavingsGoalRepository extends JpaRepository<SavingsGoal, Long> {

    // Récupérer tous les savings goals d'un utilisateur
    List<SavingsGoal> findByUser_Id(Long userId);

    // Récupérer un objectif uniquement s'il appartient à l'utilisateur demandé.
    Optional<SavingsGoal> findByIdAndUser_Id(Long id, Long userId);
}
