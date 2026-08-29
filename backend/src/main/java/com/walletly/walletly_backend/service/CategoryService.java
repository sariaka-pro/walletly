// On définit le package
package com.walletly.walletly_backend.service;

// On importe le Repository pour pouvoir l'utiliser
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.walletly.walletly_backend.exception.ErrorMessages;
import com.walletly.walletly_backend.exception.ForbiddenException;
import com.walletly.walletly_backend.exception.NotFoundException;
import com.walletly.walletly_backend.model.Category;
import com.walletly.walletly_backend.model.User;
import com.walletly.walletly_backend.repository.CategoryRepository;

@Service // On dit à Spring : “Cette classe est un service, gère-la automatiquement.”
public class CategoryService {

    // 1 - On déclare une variable qui va contenir le repository.
    private final CategoryRepository categoryRepository;
    private final InputSanitizer inputSanitizer;

    // 2- On demande à Spring d'injecter automatiquement le repository
    public CategoryService(CategoryRepository categoryRepository, InputSanitizer inputSanitizer) {
        this.categoryRepository = categoryRepository; // donc ici, on dit que la variable categoryRepository = au
                                                      // paramètre du constructeur categoryRepository
        this.inputSanitizer = inputSanitizer;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ForbiddenException("User is not authenticated");
        }
        Object principal = authentication.getPrincipal();
        if (principal == null || !(principal instanceof User)) {
            throw new ForbiddenException("Invalid user principal");
        }
        return (User) principal;
    }

    // 3- Méthode pour récupérer toutes les catégories
    public List<Category> getAllCategories() {
        return categoryRepository.findByUser(getCurrentUser());
    }

    // 4- Méthode pour créer une nouvelle catégorie ou update si la catégorie existe
    // déjà (recherche par getById()).
    public Category createCategory(Category customCategory) {
        customCategory.setName(inputSanitizer.sanitizePlainText(customCategory.getName(), "category.name"));
        customCategory.setColor(inputSanitizer.sanitizePlainText(customCategory.getColor(), "category.color"));
        customCategory.setUser(getCurrentUser());
        return categoryRepository.save(customCategory);
    }

    // 5. supprimer une catégorie
    public void deleteCategory(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.CATEGORY_NOT_FOUND));

        User currentUser = getCurrentUser();

        if (!category.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException(ErrorMessages.ACCESS_DENIED);
        }
        categoryRepository.delete(category);
    }
}
