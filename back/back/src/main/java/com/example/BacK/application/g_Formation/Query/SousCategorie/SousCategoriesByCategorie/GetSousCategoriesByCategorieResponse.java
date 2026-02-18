package com.example.BacK.application.g_Formation.Query.SousCategorie.SousCategoriesByCategorie;

import com.example.BacK.application.models.g_formation.CategorieDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetSousCategoriesByCategorieResponse {

    private Long idSousCategorie;
    private String nom;
    private String description;
    private CategorieDTO categorie;

}
