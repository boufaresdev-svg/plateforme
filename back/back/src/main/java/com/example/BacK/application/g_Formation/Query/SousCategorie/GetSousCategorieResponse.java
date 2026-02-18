package com.example.BacK.application.g_Formation.Query.SousCategorie;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetSousCategorieResponse {

    private Long idSousCategorie;
    private String nom;
    private String description;
    private Long idCategorie;
    private CategorieInfo categorie;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CategorieInfo {
        private Long idCategorie;
        private String nom;
    }

}
