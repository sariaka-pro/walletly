// Étape 1 : Déclarer le package controller
package com.walletly.walletly_backend.controller; 

// Étape 2 : Importer les outils
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.walletly.walletly_backend.service.CategoryService;

import jakarta.validation.Valid;

import java.util.List;
import com.walletly.walletly_backend.model.Category;

// Étape 3 : Annoter la classe
@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    
    private final CategoryService categoryService; 

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping 
    public List<Category> getAllCategories() { 
        return categoryService.getAllCategories(); 
    }

    @PostMapping
    public Category createCategory(@Valid @RequestBody Category category) { 
        return categoryService.createCategory(category); 
    }   

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory (@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build(); 
    }

}