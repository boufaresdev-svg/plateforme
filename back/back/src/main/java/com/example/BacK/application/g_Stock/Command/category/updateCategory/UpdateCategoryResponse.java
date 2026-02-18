package com.example.BacK.application.g_Stock.Command.category.updateCategory;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateCategoryResponse {
    private String message;
    
    public UpdateCategoryResponse(String id) {
        this.message = "Catégorie mise à jour avec succès avec l'ID : " + id;
    }
}