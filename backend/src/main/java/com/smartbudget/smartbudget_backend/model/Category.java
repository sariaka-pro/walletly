/// étape 1 : déclarer le package dans lequel la classe de mon proojet se trouve 
package com.smartbudget.smartbudget_backend.model; 


import com.smartbudget.smartbudget_backend.exception.ErrorMessages;

// étape 2 : impoter les outils nécessaires 
import jakarta.persistence.*; // C’est la boîte à outils qui permet de transformer une classe Java en table SQL (permet d'utiliser @Entity , @Table , @Id , @GenerateValue etc). 
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

// L'ordre 

@Entity
@Table(name="categories")

/// METHODE LOMBOK (permet d'avoir une ligne de code plus lisible et courte) >> les détails getters setter etc dispo dans target/../model/Category.class
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) /// Cette partie explique = Comment la base de données doit générer l’ID ? "GenerationType.IDENTITY" = la base gère l’auto‑incrément
    private Long id; 

    @Column(name="name",nullable = false) /// l’annotation JPA qui dit “cette colonne est obligatoire.
    @NotBlank(message = ErrorMessages.CATEGORY_NAME_REQUIRED) 
    @Size(max = 50, message = ErrorMessages.CATEGORY_NAME_LENGTH)
    private String name;

    @Column(name="color")
    private String color; 

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false) // ← Toujours obligatoire
    private User user; // ← Chaque catégorie appartient À UN ET SEUL utilisateur
}


