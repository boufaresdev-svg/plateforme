package com.example.BacK.application.g_Formation.Query.Categorie.CategoriesByType;

import com.example.BacK.application.models.g_formation.TypeDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetCategoriesByTypeResponse {
    private Long idCategorie;
    private String nom;
    private String description;
    private TypeDTO type;
}
