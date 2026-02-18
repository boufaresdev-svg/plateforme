package com.example.BacK.application.g_Stock.Command.category.deleteCategory;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteCategoryCommand {
    
    @NotBlank(message = "L'ID de la catégorie est obligatoire")
    private String id;
}