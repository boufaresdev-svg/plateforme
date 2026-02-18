package com.example.BacK.application.g_Formation.Command.Categorie.addCategorie;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddCategorieCommand {

    private String nom;
    private String description;
    private Long idType;



}
