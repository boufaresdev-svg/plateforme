package com.example.BacK.application.g_Stock.Command.category.addCategory;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddCategoryCommand {
    
    @NotBlank(message = "Le nom de la catégorie est obligatoire")
    private String nom;
    
    private String description;
    
    private Boolean estActif = true;
}