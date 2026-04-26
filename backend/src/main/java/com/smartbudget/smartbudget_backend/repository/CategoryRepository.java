/// Le Repository c’est la couche qui permet à ton application de parler avec la base de données.
/// C'est une interface qui dit = “Génère automatiquement toutes les opérations CRUD pour l’entité Category.”
/// findAll() → récupérer toutes les catégories // findById() → récupérer une catégorie  // save() → créer ou modifier une catégorie // deleteById() → supprimer une catégorie

// Etape 1 : Déclarer le package repository
package com.smartbudget.smartbudget_backend.repository;


// Etape 2 : Importer les outils nécessaires (Spring Data JPA)
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smartbudget.smartbudget_backend.model.Category;
import com.smartbudget.smartbudget_backend.model.User;
import java.util.List;


// Etape 3 : Créer une interface nommée CategoryRepository
// Etape 4 : Dire que cette interface hérite d'une interface magique de Spring → cette interface fournit automatiquement les méthodes CRUD
// Etape 5 : Préciser que cette interface gère l'entité Category → et que l'identifiant (clé primaire) est un Long
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUser(User user);

    // Compte le nombre de catégories créées par un utilisateur.
    long countByUser_Id(Long userId);
}






