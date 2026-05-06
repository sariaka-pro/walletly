package com.smartbudget.smartbudget_backend.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.smartbudget.smartbudget_backend.exception.ErrorMessages;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "savings_goals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavingsGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = ErrorMessages.SAVINGS_GOAL_NAME_REQUIRED)
    @Size(max = 100)
    private String name;

    @Column(nullable = false)
    @NotNull(message = ErrorMessages.SAVINGS_GOAL_TARGET_REQUIRED)
    @Positive(message = ErrorMessages.SAVINGS_GOAL_TARGET_INVALID)
    private BigDecimal targetAmount;

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal currentAmount = BigDecimal.ZERO;

    // Date limite pour atteindre l'objectif (optionnel)
    private LocalDate deadline;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
