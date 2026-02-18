package com.example.BacK.application.g_Stock.Command.category.updateCategory;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCategoryCommand {
    
    @NotBlank(message = "L'ID de la catégorie est obligatoire")
    private String id;
    
    @NotBlank(message = "Le nom de la catégorie est obligatoire")
    private String nom;
    
    private String description;
    
    private Boolean estActif;
}