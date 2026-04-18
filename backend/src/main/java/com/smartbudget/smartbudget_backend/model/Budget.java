package com.smartbudget.smartbudget_backend.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.NumberDeserializers.BigDecimalDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.YearMonthDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.YearMonthSerializer;
import com.smartbudget.smartbudget_backend.exception.ErrorMessages;
import com.smartbudget.smartbudget_backend.model.enums.BudgetPeriod;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "budgets", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "year_month"})
})

/// METHODE LOMBOK
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 

    @Column(name="name", nullable = false)
    @NotBlank(message = ErrorMessages.BUDGET_NAME_REQUIRED)
    @Size(max= 50)
    private String name; /// ex: "Budget Alimentation"

    @Column(nullable = false)
    @NotNull(message = ErrorMessages.BUDGET_NOT_NULL)
    @PositiveOrZero(message = ErrorMessages.BUDGET_LIMIT_INVALID)
    @JsonDeserialize(using = BigDecimalDeserializer.class) // solution pour erreur 400 bad request. 
    private BigDecimal spendingLimit; // ex: 500€ par mois

    @Column(nullable = false)
    @NotNull(message = ErrorMessages.BUDGET_NOT_NULL)
    @PositiveOrZero(message = ErrorMessages.BUDGET_LIMIT_INVALID)
    @Builder.Default
    private BigDecimal currentSpent = BigDecimal.ZERO;// ex: 250€ dépensé ce mois // SOLUTION erreur 400 Bad request

    @JsonProperty("remaining")
    private BigDecimal getRemaining() {
        return spendingLimit.subtract(currentSpent);
    }

    @JsonProperty("percentageSpent")
    private BigDecimal getPercentageSpent() {
        if(spendingLimit.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return currentSpent
            .divide(spendingLimit, 2, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100));
    }

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private BudgetPeriod period = BudgetPeriod.MONTHLY;


    @Column(name = "year_month", nullable = false)
    @NotNull(message = ErrorMessages.BUDGET_NOT_NULL)
    @JsonSerialize(using = YearMonthSerializer.class)
    @JsonDeserialize(using = YearMonthDeserializer.class)
    private YearMonth yearMonth;

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    private User user; 
    
}