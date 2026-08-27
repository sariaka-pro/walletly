package com.walletly.walletly_backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.walletly.walletly_backend.exception.ErrorMessages;
import com.walletly.walletly_backend.exception.ForbiddenException;
import com.walletly.walletly_backend.exception.NotFoundException;
import com.walletly.walletly_backend.model.SavingsGoal;
import com.walletly.walletly_backend.model.User;
import com.walletly.walletly_backend.repository.SavingsGoalRepository;

@Service
public class SavingsGoalService {

    @Autowired
    private SavingsGoalRepository savingsGoalRepository;

    @Autowired
    private AuthService authService;

    /**
     * Créer un nouveau savings goal pour un utilisateur
     */
    public SavingsGoal createSavingsGoal(SavingsGoal savingsGoal, Long userId) {
        User user = authService.getUserById(userId);
        savingsGoal.setUser(user);
        return savingsGoalRepository.save(savingsGoal);
    }

    /**
     * Récupérer tous les savings goals d'un utilisateur
     */
    public List<SavingsGoal> getAllByUser(Long userId) {
        return savingsGoalRepository.findByUser_Id(userId);
    }

    /**
     * Récupérer un savings goal par ID
     */
    public SavingsGoal getById(Long id) {
        return savingsGoalRepository.findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessages.SAVINGS_GOAL_NOT_FOUND));
    }

    /**
     * Modifier un savings goal (nom, targetAmount, currentAmount, deadline)
     */
    public SavingsGoal updateSavingsGoal(Long id, SavingsGoal updated, Long userId) {
        SavingsGoal existing = getById(id);

        // Vérifier que le savings goal appartient bien à l'utilisateur
        if (!existing.getUser().getId().equals(userId)) {
            throw new ForbiddenException(ErrorMessages.ACCESS_DENIED);
        }

        existing.setName(updated.getName());
        existing.setTargetAmount(updated.getTargetAmount());
        existing.setCurrentAmount(updated.getCurrentAmount());
        existing.setDeadline(updated.getDeadline());

        return savingsGoalRepository.save(existing);
    }

    /**
     * Supprimer un savings goal
     */
    public void deleteSavingsGoal(Long id, Long userId) {
        SavingsGoal existing = getById(id);

        if (!existing.getUser().getId().equals(userId)) {
            throw new ForbiddenException(ErrorMessages.ACCESS_DENIED);
        }

        savingsGoalRepository.delete(existing);
    }
}
