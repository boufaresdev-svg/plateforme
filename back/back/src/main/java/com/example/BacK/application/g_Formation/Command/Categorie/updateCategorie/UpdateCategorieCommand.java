package com.example.BacK.application.g_Formation.Command.Categorie.updateCategorie;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCategorieCommand {

    private Long idCategorie;
    private String nom;
    private String description;
    private Long idType;


}
