package com.example.BacK.application.g_Stock.Command.category.deleteCategory;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DeleteCategoryResponse {
    private String message;
    
    public DeleteCategoryResponse(String id) {
        this.message = "Catégorie supprimée avec succès avec l'ID : " + id;
    }
}