package com.walletly.walletly_backend.service;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.walletly.walletly_backend.exception.NotFoundException;
import com.walletly.walletly_backend.model.SavingsGoal;
import com.walletly.walletly_backend.repository.SavingsGoalRepository;

@ExtendWith(MockitoExtension.class)
class SavingsGoalServiceTest {

    @Mock
    private SavingsGoalRepository savingsGoalRepository;

    @InjectMocks
    private SavingsGoalService savingsGoalService;

    @Test
    void getByIdReturnsGoalWhenItBelongsToUser() {
        Long goalId = 50L;
        Long userId = 1L;
        SavingsGoal goal = SavingsGoal.builder().id(goalId).name("Voyage").build();
        when(savingsGoalRepository.findByIdAndUser_Id(goalId, userId)).thenReturn(Optional.of(goal));

        SavingsGoal result = savingsGoalService.getById(goalId, userId);

        assertSame(goal, result);
        verify(savingsGoalRepository).findByIdAndUser_Id(goalId, userId);
    }

    @Test
    void getByIdDoesNotExposeAnotherUsersGoal() {
        Long goalId = 50L;
        Long currentUserId = 1L;
        when(savingsGoalRepository.findByIdAndUser_Id(goalId, currentUserId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> savingsGoalService.getById(goalId, currentUserId));
        verify(savingsGoalRepository).findByIdAndUser_Id(goalId, currentUserId);
    }
}
