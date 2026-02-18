package com.example.BacK.application.g_Formation.Command.SousCategorie.UpdateSousCategorie;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateSousCategorieCommand {

    private Long idSousCategorie;
    private String nom;
    private String description;
    private Long idCategorie;
}
