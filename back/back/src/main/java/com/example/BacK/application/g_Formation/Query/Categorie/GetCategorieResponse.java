package com.example.BacK.application.g_Formation.Query.Categorie;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetCategorieResponse {

    private Long idCategorie;
    private String nom;
    private String description;
    private Long idType;
    private TypeInfo type;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TypeInfo {
        private Long idType;
        private String nom;
    }

}
