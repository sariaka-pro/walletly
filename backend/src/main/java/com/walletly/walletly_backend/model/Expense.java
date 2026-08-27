package com.walletly.walletly_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import com.walletly.walletly_backend.exception.ErrorMessages;

import java.math.BigDecimal;
import java.time.LocalDate;


@Entity
@Table(name="expense")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotNull(message = ErrorMessages.AMOUNT_REQUIRED) /// permet une validation propre côté API. 
    @Positive(message = ErrorMessages.INVALID_EXPENSE_AMOUNT) /// permet de spécifier que le montant doit être strictement > 0 (éviter les chiffres négatifs)
    private BigDecimal amount;

    @NotBlank(message = ErrorMessages.EXPENSE_DESCRIPTION_REQUIRED)
    @Size(min = 2, max = 300, message = ErrorMessages.EXPENSE_DESCRIPTION_LENGTH)
    private String description;

    @NotNull(message = ErrorMessages.EXPENSE_DATE_REQUIRED) /// Ce champs ne peut pas être null
    @PastOrPresent(message = ErrorMessages.EXPENSE_DATE_IN_FUTURE) // La date ne peut pas être une date du futur. 
    private LocalDate date;

   // @ManyToOne // que c'est relié à une autre table SQL
    /// @JoinColumn(name = "category_id") /// l'élément qui relie category à l'id de category dans SQL. 
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    private User user; 

    @ManyToOne
    @JoinColumn(name = "budget_id", nullable = false)
    private Budget budget;

}
